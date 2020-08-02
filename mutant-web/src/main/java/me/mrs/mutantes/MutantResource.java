package me.mrs.mutantes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Path(MutantResource.PATH)
@Singleton
public class MutantResource {
    private Logger logger = LoggerFactory.getLogger("controller");

    @SuppressWarnings("squid:S1075")
    public static final String PATH = "/mutant/";
    private final DnaEvaluator dnaEvaluator;
    private EvaluationsService evaluationsService;
    private ModelMapper modelMapper;
    private final Executor executor;

    @Inject
    public MutantResource(
            @Nonnull DnaEvaluator dnaEvaluator,
            @Nonnull EvaluationsService evaluationsService,
            @Nonnull ModelMapper modelMapper,
            @ServiceExecutor @Nonnull Executor executor
    ) {
        this.dnaEvaluator = dnaEvaluator;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
        this.executor = executor;
    }

    // FIXME: Habilitar @Valid nuevamente
    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void isMutant(
            @Suspended AsyncResponse asyncResponse, @Valid final DnaViewModel payload
    ) {
        logger.trace("mutant requested");
        CompletableFuture
                .supplyAsync(() -> dnaEvaluator.isMutant(payload.getDna()), executor)
                .thenAccept(isMutant -> CompletableFuture.allOf(registerEvaluation(payload,
                        isMutant
                ), response(asyncResponse, isMutant)))
                .exceptionally(ex -> {
                    asyncResponse.resume(ex);
                    return null;
                });
    }

    public CompletableFuture<Void> response(
            @Suspended AsyncResponse asyncResponse, Boolean isMutant
    ) {
        return CompletableFuture.runAsync(() -> {
            if (Boolean.TRUE.equals(isMutant)) {
                asyncResponse.resume(new WebApplicationException(Response.Status.FORBIDDEN));
            } else {
                asyncResponse.resume(new WebApplicationException(Response.Status.OK));
            }
        });
    }

    public CompletableFuture<Void> registerEvaluation(
            @Valid DnaViewModel payload, Boolean isMutant
    ) {
        return CompletableFuture.runAsync(() -> evaluationsService.registerEvaluation(modelMapper.toBusinessModel(payload,
                isMutant
        )));
    }

}
