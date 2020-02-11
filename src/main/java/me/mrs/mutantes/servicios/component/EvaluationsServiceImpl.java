package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.EvaluationsService;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Integer.max;

@Service
public class EvaluationsServiceImpl implements EvaluationsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue<EvaluationModel> queue;
    private final EvaluationsRepository repository;
    private final Lock lock = new ReentrantLock();
    private final Condition queueAvailable = lock.newCondition();
    private final long durationToRetryMs;
    private boolean running;

    public EvaluationsServiceImpl(
            @Value("${application.evaluations.persistence.queue.capacity}") int capacity,
            @Value("${application.evaluations.persistence.queue.retryMs}") long durationToRetryMs,
            @NonNull EvaluationsRepository repository,
            @NonNull Executor executorService) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.repository = repository;
        this.durationToRetryMs = durationToRetryMs;
        start();
        executorService.execute(this::flushToRepo);
    }

    private void flushToRepo() {
        try {
            while (isRunning()) {
                var evaluationModels = getEvaluationModels();
                if (!evaluationModels.isEmpty() && !tryBatchInsert(evaluationModels)) {
                    submitToRetry(evaluationModels);
                }
            }
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    boolean isRunning() {
        return running && !Thread.interrupted();
    }

    void start() {
        this.running = true;
    }

    private void submitToRetry(List<EvaluationModel> evaluations) throws InterruptedException {
        Thread.sleep(durationToRetryMs);
        queue.addAll(evaluations);
        lock.lock();
        try {
            queueAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    private List<EvaluationModel> getEvaluationModels() throws InterruptedException {
        var evaluations = new ArrayList<EvaluationModel>(queue.size());
        while (queue.drainTo(evaluations, max(repository.getSupportedBatchSize(), 1)) == 0) {
            lock.lock();
            try {
                queueAvailable.await();
            } finally {
                lock.unlock();
            }
        }
        return evaluations;
    }

    private boolean tryBatchInsert(List<EvaluationModel> queries) {
        try {
            repository.batchInsert(queries);
            return true;
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    @Override
    public void registerEvaluation(EvaluationModel evaluation) {
        lock.lock();
        try {
            queue.add(evaluation);
            queueAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}