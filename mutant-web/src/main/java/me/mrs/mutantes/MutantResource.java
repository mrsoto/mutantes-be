package me.mrs.mutantes;

import org.glassfish.jersey.server.ManagedAsync;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(MutantResource.PATH)
public class MutantResource {
    @SuppressWarnings("squid:S1075")
    public static final String PATH = "/mutant/";
    private final DnaEvaluator dnaEvaluator;
    private EvaluationsService evaluationsService;
    private ModelMapper modelMapper;

    @Inject
    public MutantResource(
            @NotNull DnaEvaluator dnaEvaluator,
            @NotNull EvaluationsService evaluationsService,
            @NotNull ModelMapper modelMapper) {
        this.dnaEvaluator = dnaEvaluator;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void isMutant(
            @Suspended AsyncResponse asyncResponse, @Valid final DnaViewModel payload) {
//        final Set<ConstraintViolation<DnaViewModel>> validationResult = dnaValidator.validate(
//                payload,
//                DnaViewModel.class);
//        if (!validationResult.isEmpty()) {
//            var errorMessage = validationResult
//                    .stream()
//                    .map(ConstraintViolation::getMessage)
//                    .collect(Collectors.joining());
//            asyncResponse.resume(new WebApplicationException(errorMessage,
//                    Response.Status.BAD_REQUEST));
//        }

        boolean isMutant = dnaEvaluator.isMutant(payload.getDna());
        evaluationsService.registerEvaluation(modelMapper.toBusinessModel(payload, isMutant));
        if (isMutant) {
            asyncResponse.resume(new WebApplicationException(Response.Status.FORBIDDEN));
        } else {
            asyncResponse.resume(new WebApplicationException(Response.Status.OK));
        }
    }
}
