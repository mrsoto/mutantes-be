package me.mrs.mutantes.services;

import me.mrs.mutantes.EvaluationsRepository;
import me.mrs.mutantes.StatsModel;
import me.mrs.mutantes.StatsService;
import me.mrs.mutantes.annotaion.Now;
import me.mrs.mutantes.annotaion.StatsCacheLifeSpan;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Singleton
public class StatsServiceImpl implements StatsService {

    private final EvaluationsRepository repository;
    private final Duration cacheLifeSpan;
    private final Supplier<Instant> nowSupplier;
    private final Lock lock = new ReentrantLock();
    private StatsModel cachedStats;
    private Instant timestamp = Instant.ofEpochMilli(0);

    @Inject
    public StatsServiceImpl(
            EvaluationsRepository repository,
            @StatsCacheLifeSpan Long cacheLifeSpan,
            @Now Supplier<Instant> nowSupplier) {
        this.repository = repository;
        this.cacheLifeSpan = Duration.ofMillis(cacheLifeSpan);
        this.nowSupplier = nowSupplier;
    }

    @Override
    @Nullable
    public StatsModel getStats() {

        if (isDeprecated()) {
            lock.lock();
            try {
                if (isDeprecated()) {
                    tryGetStats();
                }
            } finally {
                lock.unlock();
            }
        }
        return cachedStats;
    }

    public void tryGetStats() {
        final var stats = repository.getStats();
        if (stats.isPresent()) {
            cachedStats = stats.get();
            setNextDeprecationInstant();
        }
    }

    private void setNextDeprecationInstant() {
        timestamp = getNow().plus(cacheLifeSpan);
    }

    private boolean isDeprecated() {
        return timestamp.isBefore(getNow());
    }

    private Instant getNow() {
        return nowSupplier.get();
    }
}
