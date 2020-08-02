package me.mrs.mutantes.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import me.mrs.mutantes.ContainerBuilder;

import javax.annotation.Nonnull;
import java.util.List;

public final class GuiceContainerBuilder<T> implements ContainerBuilder<T> {
    private final Class<T> klass;

    public GuiceContainerBuilder(Class<T> klass) {
        this.klass = klass;
    }

    @Nonnull
    public static Iterable<AbstractModule> getAbstractModules(JerseyConfiguration configuration) {
        var apiModule = new MutantApiModule(1_000L, 1_000, 10_000);
        var webModule = new WebModule();
        var jerseyModule = new JerseyModule(configuration);
        return List.of(apiModule, webModule, jerseyModule);
    }

    @Override
    public T create(JerseyConfiguration configuration) {
        var modules = getAbstractModules(configuration);
        var injector = Guice.createInjector(Stage.PRODUCTION, modules);
        return injector.getInstance(klass);
    }

}
