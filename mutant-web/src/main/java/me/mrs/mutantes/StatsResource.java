package me.mrs.mutantes;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path(StatsResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StatsResource {
    @SuppressWarnings("squid:S1075")
    public static final String PATH = "/stats";
    private final StatsService evaluationsService;
    private final ModelMapper modelMapper;

    @Inject
    public StatsResource(
            @NotNull StatsService evaluationsService, @NotNull ModelMapper modelMapper) {
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @GET
    public void getStats(@Suspended AsyncResponse asyncResponse) {
        StatsViewModel body = modelMapper.toViewModel(evaluationsService.getStats());

        asyncResponse.resume(body);
    }
}
