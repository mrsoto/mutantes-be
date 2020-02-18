package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.Valid;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

@Service
public class EvaluationsServiceImpl implements EvaluationsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue<EvaluationModel> queue;
    private final EvaluationsRepository repository;
    private final long durationToRetryMs;
    private final int bufferDurationMs;
    private final int numRetries;
    private final FluxFromQueue<EvaluationModel> fluxCreator;
    private final Executor executorService;
    private final Scheduler parallelScheduler;
    private Disposable saveSubscription;

    public EvaluationsServiceImpl(
            @Value("${application.evaluations.persistence.queue.capacity}") int capacity,
            @Value("${application.evaluations.persistence.buffer.durationMs:100}") int bufferDurationMs,
            @Value("${application.evaluations.persistence.queue.retryMs}") long durationToRetryMs,
            @Value("${application.evaluations.persistence.numRetries:20}") int numRetries,
            @Valid @NonNull EvaluationsRepository repository,
            @Valid @NonNull Executor executor,
            @Valid @NonNull FluxFromQueue<EvaluationModel> fluxCreator,
            @Nullable Scheduler parallelScheduler) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.repository = repository;
        this.durationToRetryMs = durationToRetryMs;
        this.bufferDurationMs = bufferDurationMs;
        this.executorService = executor;
        this.parallelScheduler = Optional
                .ofNullable(parallelScheduler)
                .orElseGet(Schedulers::boundedElastic);
        this.numRetries = numRetries;
        this.fluxCreator = fluxCreator;
    }

    @PreDestroy
    void shutdown() {
        saveSubscription.dispose();
    }

    @PostConstruct
    void init() {

        ParallelFlux<Integer> saved = createSavingFlux();

        saveSubscription = saved.subscribe(n -> logger.info("Saved {} evaluations", n),
                error -> logger.error("ERROR SAVING:", error));

        Flux
                .interval(Duration.ofSeconds(1))
                .map(i -> queue.size())
                .filter(i -> i > 0)
                .subscribe(size -> logger.info("Queue size: {}", size));
    }

    private ParallelFlux<Integer> createSavingFlux() {
        final var saveRequest = Flux
                .create(fluxCreator.create(executorService, queue))
                .buffer(Duration.ofMillis(bufferDurationMs))
                .parallel()
                .runOn(parallelScheduler)
                .doOnNext(l -> logger.info("Buffer size received: {}", l.size()))
                .filter(Predicate.not(List::isEmpty))
                .map(Mono::just);

        return saveRequest.flatMap(evaluations -> evaluations
                .map(this::save)
                .onErrorResume(error -> evaluations
                        .delayElement(Duration.ofMillis(durationToRetryMs))
                        .cache()
                        .log()
                        .map(this::save) // Better if Redis queue is included here
                        .retryBackoff(numRetries - 1L, Duration.ofMillis(durationToRetryMs))));
    }

    private int save(List<EvaluationModel> evaluationModels) {
        try {
            repository.batchInsert(evaluationModels);
            return evaluationModels.size();
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
    }

    @Override
    public void registerEvaluation(EvaluationModel evaluation) {
        queue.add(evaluation);
    }

}
