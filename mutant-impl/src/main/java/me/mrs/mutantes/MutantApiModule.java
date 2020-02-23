package me.mrs.mutantes;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import me.mrs.mutantes.annotaion.*;
import me.mrs.mutantes.persistence.HibernateEvaluationsRepository;
import me.mrs.mutantes.services.DnaEvaluatorImpl;
import me.mrs.mutantes.services.EvaluationsServiceImpl;
import me.mrs.mutantes.services.StatsServiceImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

public class MutantApiModule extends AbstractModule {
    private final String mutantsJpaPersistentUnit;
    private final Duration statsCacheLifeSpan;
    private final int persistenceCacheCapacity;
    private final int persistenceRetryMs;
    private final int insertBatchSize;

    public MutantApiModule(
            String mutantsJpaPersistentUnit) {
        this.mutantsJpaPersistentUnit = mutantsJpaPersistentUnit;

        this.statsCacheLifeSpan = Duration.ofSeconds(1);
        persistenceCacheCapacity = 10;
        persistenceRetryMs = 1000;
        insertBatchSize = 1000;
    }

    private static Supplier<Instant> nowInstanceProvider() {
        return Instant::now;
    }

    private static TypeLiteral<Supplier<Instant>> createInstantSupplierTypeLiteral() {
        return new TypeLiteral<>() {
        };
    }

    @Override
    public void configure() {
        bind(DnaEvaluator.class).to(DnaEvaluatorImpl.class).in(Singleton.class);
        bind(EvaluationsService.class).to(EvaluationsServiceImpl.class).in(Singleton.class);
        bind(StatsService.class).to(StatsServiceImpl.class).in(Singleton.class);
        bind(EvaluationsRepository.class)
                .to(HibernateEvaluationsRepository.class)
                .in(Singleton.class);
        bind(BlockingQueue.class).to(ArrayBlockingQueue.class);

        bind(createInstantSupplierTypeLiteral())
                .annotatedWith(Now.class)
                .toProvider(MutantApiModule::nowInstanceProvider);
        bind(EntityManagerFactory.class)
                .toProvider(this::entityManagerFactoryProvider)
                .in(Singleton.class);

        bindConstant().annotatedWith(EvaluationPersistentUnit.class).to(mutantsJpaPersistentUnit);
        bindConstant().annotatedWith(PersistenceQueueCapacity.class).to(persistenceCacheCapacity);
        bindConstant().annotatedWith(PersistenceRetryMs.class).to(persistenceRetryMs);
        bindConstant().annotatedWith(InsertBatchSize.class).to(insertBatchSize);
        bindConstant().annotatedWith(StatsCacheLifeSpan.class).to(statsCacheLifeSpan.toMillis());
    }

    private EntityManagerFactory entityManagerFactoryProvider() {
        return Persistence.createEntityManagerFactory(mutantsJpaPersistentUnit);
    }

}
