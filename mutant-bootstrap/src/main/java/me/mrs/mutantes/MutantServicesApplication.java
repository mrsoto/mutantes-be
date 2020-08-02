package me.mrs.mutantes;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import me.mrs.mutantes.services.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class MutantServicesApplication {
    public static final int DEFAULT_PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(MutantServicesApplication.class);

    @SuppressWarnings("java:S1450")
    private Startable evaluationsService;
    @SuppressWarnings("java:S1450")
    private JerseyServer server;

    public static void main(String[] args) {
        int port = getServerPort();
        var containerBuilder = ContainerFactory.of(MutantServicesApplication.class);
        var jerseyConfiguration = createJerseyConfiguration(port);
        var application = containerBuilder.create(jerseyConfiguration);
        try {
            application.evaluationsService.start();
            application.server.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(255);
        }
    }

    public static int getServerPort() {
        return Optional
                .ofNullable(System.getenv("PORT"))
                .map(Integer::valueOf)
                .orElse(DEFAULT_PORT);
    }

    public static JerseyConfiguration createJerseyConfiguration(int port) {
        return JerseyConfiguration
                .builder()
                .addPackage(true, MutantResource.class.getPackageName())
                .registerClasses(JacksonJsonProvider.class)
                .addPort(port)
                .addResourceClass(StatsViewModel.class)
                .addResourceClass(DnaViewModel.class)
                .build();
    }

    @Inject
    public void installEvaluationService(EvaluationsService evaluationsService) {
        this.evaluationsService = (Startable) evaluationsService;
    }

    @Inject
    public void installJerseyServer(JerseyServer server) {this.server = server;}
}
