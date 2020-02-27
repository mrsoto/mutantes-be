package me.mrs.mutantes;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import me.mrs.mutantes.services.EvaluationsServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class MutantServicesApplication {
    public static final int DEFAULT_PORT = 8080;
    public static final String MUTANTS_JPA_PERSISTENT_UNIT = "mutantes";
    private static final Logger LOGGER = LoggerFactory.getLogger(MutantServicesApplication.class);

    public static void main(String[] args) {
        int port = getPort();
        JerseyConfiguration configuration = getJerseyConfiguration(port);
        var modules = getAbstractModules(configuration);
        var injector = Guice.createInjector(Stage.PRODUCTION, modules);
        try {
            injector.getInstance(EvaluationsServiceImpl.class).start();
            injector.getInstance(JerseyServer.class).start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(255);
        }
    }

    @NotNull
    public static Iterable<AbstractModule> getAbstractModules(JerseyConfiguration configuration) {
        var apiModule = new MutantApiModule(1_000L, 1_000, 10_000);
        var webModule = new WebModule();
        var jerseyModule = new JerseyModule(configuration);
        return List.of(apiModule, webModule, jerseyModule);
    }

    public static JerseyConfiguration getJerseyConfiguration(int port) {
        return JerseyConfiguration
                .builder()
                .addPackage(true, MutantResource.class.getPackageName())
                .registerClasses(JacksonJsonProvider.class)
                .addPort(port)
                .addResourceClass(StatsViewModel.class)
                .addResourceClass(DnaViewModel.class)
                .build();
    }

    public static int getPort() {
        return Optional
                .ofNullable(System.getenv("PORT"))
                .map(Integer::valueOf)
                .orElse(DEFAULT_PORT);
    }

}
