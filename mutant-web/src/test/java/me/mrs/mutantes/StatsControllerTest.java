package me.mrs.mutantes;

import me.mrs.mutantes.entity.StatsModelEntity;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.doReturn;

@DisplayName("GIVEN a Stats Controller")
class StatsControllerTest {

    EvaluationsRepository repository;

    @Test
    @DisplayName("WHEN stats are queried THEN should response HTTP_STATUS.OK ans proper JSON")
    public void genStatsAreRetrieved() throws Exception {
        StatsModel stats = new StatsModelEntity(10, 4);
        doReturn(stats).when(repository).getStats();
        JerseyServerSupplier.createServerAndTest(getResourceConfig(), server -> {
            final var response = server.path(StatsResource.PATH).request().get();
            final var status = response.getStatusInfo();
            Assertions.assertEquals(Response.Status.OK, status);
            final var entity = response.getEntity();
            Assertions.assertEquals(entity, new StatsViewModel(10, 4));
        });
    }

    private ResourceConfig getResourceConfig() {
        return new ResourceConfig().registerClasses(StatsResource.class);
    }

}