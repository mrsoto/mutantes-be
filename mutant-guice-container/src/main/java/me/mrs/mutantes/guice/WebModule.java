package me.mrs.mutantes.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.mrs.mutantes.ModelMapper;
import me.mrs.mutantes.ServiceExecutor;
import me.mrs.mutantes.annotaion.DatabaseUrl;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WebModule extends AbstractModule {
    private static final String DEFAULT_H2_DATABASE =
            "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM " + "'classpath:scripts/createh2.sql'";

    @Override
    protected void configure() {
        final String databaseUrl = Optional
                .ofNullable(System.getenv("JDBC_DATABASE_URL"))
                .orElse(DEFAULT_H2_DATABASE);
        bind(ModelMapper.class);
        bind(BlockingQueue.class).toProvider(() -> new ArrayBlockingQueue<>(1000));
        bind(Executor.class)
                .annotatedWith(ServiceExecutor.class)
                .toProvider(Executors::newCachedThreadPool);
        bindConstant().annotatedWith(DatabaseUrl.class).to(databaseUrl);
    }

    @Provides
    Validator validatorSupplier() {
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        // TODO: Close validator
        return validatorFactory.getValidator();
    }

    @Provides
    @Inject
    DataSource dataSource(@Nullable @DatabaseUrl String dbUrl) {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }
}
