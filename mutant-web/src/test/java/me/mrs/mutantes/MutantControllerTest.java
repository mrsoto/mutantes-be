package me.mrs.mutantes;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.mrs.mutantes.entity.EvaluationModelEntity;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("GIVEN a Human Controller")
@ExtendWith(MockitoExtension.class)
class MutantControllerTest {

    EvaluationsService evaluationsService;
    @Captor
    ArgumentCaptor<EvaluationModel> evaluationModelArgumentCaptor;

    static private Stream<Collection<String>> mutantSamples() {
        // @formatter:off
        List<String> horizontalCAndDiagonalA = Arrays.asList(
                "ATGAGT",
                "TAGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCATTG");
        List<String> VerticalGHorizintalA = Arrays.asList(
                "CTGCGA",
                "ACATGC",
                "ATCTGT",
                "AAAAGA",
                "GATGCA",
                "TCACTG");
        // @formatter:on
        return Stream.of(VerticalGHorizintalA, horizontalCAndDiagonalA);
    }

    static private Stream<Collection<String>> invalidDnaSamples() {
        List<String> emptyDna = Collections.emptyList();
        List<String> differentSizes = Arrays.asList("A", "TAGTGC");
        List<String> nullValues = Arrays.asList("CTGCGA", null, "ATCTGT");
        List<String> invalidSymbol = Arrays.asList("XAAAA", "AAAAA");
        return Stream.of(emptyDna, differentSizes, nullValues, invalidSymbol);
    }

    @Test
    @DisplayName("WHEN a Human is inquired THEN should response HTTP_STATUS.OK")
    public void whenHumanIsInquiredThenShouldResponseOk() throws Exception {
        List<String> dnaSequence = Arrays.asList("AAAA", "CAGT", "TTAT", "TTAG");
        var requestPayload = Collections.singletonMap("dna", dnaSequence);
        JerseyServerSupplier.createServerAndTest(getResourceConfig(),
                server -> whenDnaIsEvaluated(dnaSequence,
                        () -> server
                                .path(MutantResource.PATH)
                                .request()
                                .post(Entity.entity(requestPayload,
                                        MediaType.APPLICATION_JSON_TYPE))
                                .readEntity(String.class),
                        false));

    }

    private ResourceConfig getResourceConfig() {
        return new ResourceConfig().registerClasses(MutantResource.class);
    }

    @DisplayName("WHEN a Mutant is inquired")
    @ParameterizedTest(name = "AND the adn is like {0} THEN should response HTTP_STATUS.Forbidden")
    @MethodSource("mutantSamples")
    public void whenAMutantIsInquiredThenShouldResponseForbidden(List<String> dna) throws Exception {
        var requestPayload = Collections.singletonMap("dna", dna);
        JerseyServerSupplier.createServerAndTest(getResourceConfig(),
                server -> whenDnaIsEvaluated(dna,
                        () -> server
                                .path(MutantResource.PATH)
                                .request()
                                .post(Entity.entity(requestPayload,
                                        MediaType.APPLICATION_JSON_TYPE))
                                .readEntity(String.class),
                        true));
    }

    @DisplayName("WHEN a DNA is inquired")
    @ParameterizedTest(name = "AND is an invalid DNA sequence like {0} THEN should response " +
            "HTTP_STATUS.BadRequest")
    @MethodSource("invalidDnaSamples")
    public void whenInvalidDndIsInquired(List<String> dna) throws Exception {
        var objectMapper = new ObjectMapper();
        var requestPayload = Collections.singletonMap("dna", dna);
        JerseyServerSupplier.createServerAndTest(getResourceConfig(),
                server -> whenDnaCantBeEvaluated(dna,
                        () -> server
                                .path(MutantResource.PATH)
                                .request()
                                .post(Entity.entity(requestPayload,
                                        MediaType.APPLICATION_JSON_TYPE))
                                .readEntity(String.class)));
    }

    private void whenDnaCantBeEvaluated(
            List<String> dnaSequence, Callable<?> evaluation) throws Exception {
        Instant start = Instant.now();
        evaluation.call();
        Instant end = Instant.now();

        verifyNoInteractions(evaluationsService);
    }

    private void whenDnaIsEvaluated(
            List<String> dnaSequence, Callable<?> evaluation, boolean isMutant) throws Exception {
        Instant start = Instant.now();
        evaluation.call();
        Instant end = Instant.now();

        verify(evaluationsService).registerEvaluation(any(EvaluationModelEntity.class));
        verify(evaluationsService).registerEvaluation(evaluationModelArgumentCaptor.capture());
        EvaluationModel evaluationModel = evaluationModelArgumentCaptor.getValue();

        assertAll("Should save evaluation",
                () -> assertEquals(evaluationModel.getDna(), dnaSequence),
                () -> assertEquals(evaluationModel.isMutant(), isMutant),
                () -> assertAll("Instant in range",
                        () -> Assertions.assertTrue(start.isBefore(evaluationModel.getTimestamp()) || start
                                .equals(evaluationModel.getTimestamp())),
                        () -> Assertions.assertTrue(end.isAfter(evaluationModel.getTimestamp()) || end
                                .equals(evaluationModel.getTimestamp()))));
    }

}