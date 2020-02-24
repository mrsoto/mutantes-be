package me.mrs.mutantes.services;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.EvaluationsRepository;
import me.mrs.mutantes.EvaluationsService;
import me.mrs.mutantes.annotaion.EvaluationExecutor;
import me.mrs.mutantes.annotaion.EvaluationQueue;
import me.mrs.mutantes.annotaion.PersistenceRetryMs;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class EvaluationsServiceImpl implements EvaluationsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue<EvaluationModel> queue;
    private final EvaluationsRepository repository;
    private final long durationToRetryMs;

    @Inject
    public EvaluationsServiceImpl(
            @PersistenceRetryMs long durationToRetryMs,
            @NotNull EvaluationsRepository repository,
            @EvaluationExecutor @NotNull Executor executor,
            @EvaluationQueue @NotNull BlockingQueue<EvaluationModel> queue) {
        this.repository = repository;
        this.durationToRetryMs = durationToRetryMs;
        this.queue = queue;
        // FIXME: Don't start thread in constructor
        executor.execute(this::flushToRepo);
    }

    void flushToRepo() {
        try {
            while (isRunning()) {
                var evaluationModels = getEvaluationModels();
                if (!evaluationModels.isEmpty() && !tryBatchInsert(evaluationModels)) {
                    submitToRetry(evaluationModels);
                }
            }
        } catch (InterruptedException e) {
            logger.info("Finish");
            Thread.currentThread().interrupt();
        }
    }

    boolean isRunning() {
        return !Thread.interrupted();
    }

    private void submitToRetry(List<EvaluationModel> evaluations) throws InterruptedException {
        Thread.sleep(durationToRetryMs);
        queue.addAll(evaluations);
    }

    private List<EvaluationModel> getEvaluationModels() throws InterruptedException {
        var evaluations = new ArrayList<EvaluationModel>(queue.size());
        evaluations.add(queue.take());
        evaluations.ensureCapacity(queue.size() + 1);
        queue.drainTo(evaluations);
        return evaluations;
    }

    private boolean tryBatchInsert(List<EvaluationModel> queries) {
        try {
            repository.batchInsert(queries);
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void registerEvaluation(@NotNull EvaluationModel evaluation) {
        queue.add(evaluation);
    }
}
