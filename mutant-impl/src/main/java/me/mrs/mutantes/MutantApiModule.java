package me.mrs.mutantes;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import me.mrs.mutantes.annotaion.*;
import me.mrs.mutantes.converter.AttributeConverter;
import me.mrs.mutantes.converter.BaseDnaConverter;
import me.mrs.mutantes.persistence.JdbcEvaluationsRepository;
import me.mrs.mutantes.persistence.JdbcTemplate;
import me.mrs.mutantes.services.DnaEvaluatorImpl;
import me.mrs.mutantes.services.EvaluationsServiceImpl;
import me.mrs.mutantes.services.StatsServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MutantApiModule extends AbstractModule {
    private final Duration statsCacheLifeSpan;
    private final Long persistenceRetryMs;
    private final int insertBatchSize;
    private final int queueCapacity;

    public MutantApiModule(
            long persistenceRetryMs, int insertBatchSize, int queueCapacity) {
        this.statsCacheLifeSpan = Duration.ofSeconds(5);
        this.persistenceRetryMs = persistenceRetryMs;
        this.insertBatchSize = insertBatchSize;
        this.queueCapacity = queueCapacity;
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
        bind(JdbcTemplate.class).in(Singleton.class);
        bind(DnaEvaluator.class).to(DnaEvaluatorImpl.class).in(Singleton.class);
        bind(EvaluationsService.class).to(EvaluationsServiceImpl.class).in(Singleton.class);
        bind(StatsService.class).to(StatsServiceImpl.class).in(Singleton.class);
        bind(getTypeLiteralDnaConverter())
                .annotatedWith(DnaConverter.class)
                .to(BaseDnaConverter.class)
                .in(Singleton.class);
        bind(EvaluationsRepository.class).to(JdbcEvaluationsRepository.class).in(Singleton.class);
        bind(createTypeLiteralEvaluationQueue())
                .annotatedWith(EvaluationQueue.class)
                .toProvider(() -> new ArrayBlockingQueue<>(queueCapacity));

        bind(createInstantSupplierTypeLiteral())
                .annotatedWith(Now.class)
                .toProvider(MutantApiModule::nowInstanceProvider);

        bind(Executor.class)
                .annotatedWith(EvaluationExecutor.class)
                .toProvider(Executors::newSingleThreadExecutor);

        bindConstant().annotatedWith(PersistenceRetryMs.class).to(persistenceRetryMs);
        bindConstant().annotatedWith(InsertBatchSize.class).to(insertBatchSize);
        bindConstant().annotatedWith(StatsCacheLifeSpan.class).to(statsCacheLifeSpan.toMillis());
    }

    public TypeLiteral<AttributeConverter<List<String>, String>> getTypeLiteralDnaConverter() {
        return new TypeLiteral<>() {
        };
    }

    public TypeLiteral<BlockingQueue<EvaluationModel>> createTypeLiteralEvaluationQueue() {
        return new TypeLiteral<>() {
        };
    }

}
