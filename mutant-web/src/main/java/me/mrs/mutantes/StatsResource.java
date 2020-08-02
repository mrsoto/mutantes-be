package me.mrs.mutantes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Path(StatsResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class StatsResource {
    private Logger logger = LoggerFactory.getLogger("controller");

    @SuppressWarnings("squid:S1075")
    public static final String PATH = "/stats";
    private final StatsService evaluationsService;
    private final ModelMapper modelMapper;
    private final Executor executor;

    @Inject
    public StatsResource(
            @Nonnull StatsService evaluationsService,
            @Nonnull ModelMapper modelMapper,
            @ServiceExecutor @Nonnull Executor executor
    ) {
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
        this.executor = executor;
    }

    @GET
    public void getStats(@Suspended AsyncResponse asyncResponse) {
        CompletableFuture.supplyAsync(() -> {
            logger.trace("Stats requested");
            return modelMapper.toViewModel(evaluationsService.getStats());
        }, executor).thenApply(asyncResponse::resume);
    }
}
