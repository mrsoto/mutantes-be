package me.mrs.mutantes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

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
    private final Validator validator;

    @Inject
    public MutantResource(
            @Nonnull DnaEvaluator dnaEvaluator,
            @Nonnull EvaluationsService evaluationsService,
            @Nonnull ModelMapper modelMapper,
            @ServiceExecutor @Nonnull Executor executor,
            @Nonnull Validator validator
    ) {
        this.dnaEvaluator = dnaEvaluator;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
        this.executor = executor;
        this.validator = validator;
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    // TODO: AOP Validation
    public void isMutant(
            @Suspended AsyncResponse asyncResponse, final DnaViewModel payload
    ) {
        logger.trace("mutant requested");
        CompletableFuture
                .runAsync(validateRequest(payload), executor)
                .thenApply(z -> dnaEvaluator.isMutant(payload.getDna()))
                .thenAccept(isMutant -> CompletableFuture.allOf(registerEvaluation(payload,
                        isMutant
                ), respond(asyncResponse, isMutant)))
                .exceptionally(ex -> {
                    asyncResponse.resume(ex.getCause());
                    return null;
                });
    }

    public Runnable validateRequest(DnaViewModel payload) {
        return () -> {
            Set<ConstraintViolation<DnaViewModel>> violations = validator.validate(payload);
            if (!violations.isEmpty()) {
                final var message = violations
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining());
                logger.error("Invalid request {}: {}", payload.getDna(), message);
                throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
            }
        };
    }

    public CompletableFuture<Void> respond(
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
