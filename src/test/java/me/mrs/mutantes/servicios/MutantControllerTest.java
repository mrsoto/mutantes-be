package me.mrs.mutantes.servicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.mrs.mutantes.servicios.component.*;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@WebFluxTest(MutantController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("GIVEN a Human Controller")
class MutantControllerTest {

    @MockBean
    EvaluationsService evaluationsService;
    @Captor
    ArgumentCaptor<EvaluationModel> evaluationModelArgumentCaptor;
    @Autowired
    private WebTestClient webTestClient;

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
        var objectMapper = new ObjectMapper();
        List<String> dnaSequence = Arrays.asList("AAAA", "CAGT", "TTAT", "TTAG");
        var requestPayload = Collections.singletonMap("dna", dnaSequence);

        webTestClient
                .post()
                .uri("/mutant/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestPayload))
                .exchange()
                .expectStatus()
                .isOk();

    }

    public void whenDnaIsEvaluated(
            List<String> dnaSequence, Callable<?> evaluation, boolean isMutant) throws Exception {
        Instant start = Instant.now();
        evaluation.call();
        Instant end = Instant.now();

        verify(evaluationsService).registerEvaluation(any(EvaluationModel.class));
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

    @DisplayName("WHEN a Mutant is inquired")
    @ParameterizedTest(name = "AND the adn is like {0} THEN should response HTTP_STATUS.Forbidden")
    @MethodSource("mutantSamples")
    public void whenAMutantIsInquiredThenShouldResponseForbidden(List<String> dna) throws Exception {
        var objectMapper = new ObjectMapper();
        var requestPayload = Collections.singletonMap("dna", dna);
        webTestClient
                .post()
                .uri("/mutant/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestPayload))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @DisplayName("WHEN a DNA is inquired")
    @ParameterizedTest(name = "AND is an invalid DNA sequence like {0} THEN should response " +
            "HTTP_STATUS.BadRequest")
    @MethodSource("invalidDnaSamples")
    public void whenInvalidDndIsInquired(List<String> dna) throws Exception {
        var objectMapper = new ObjectMapper();
        var requestPayload = Collections.singletonMap("dna", dna);
        webTestClient
                .post()
                .uri("/mutant/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestPayload))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @TestConfiguration
    static class Config {
        @Bean
        DnaEvaluator dnaEvaluator() {
            return new DnaEvaluatorImpl();
        }

        @Bean
        ModelMapper modelMapper() {
            return new ModelMapper();
        }

        @Bean
        EvaluationFacade evaluationFacade(EvaluationsService evaluationsService) {
            return new EvaluationFacadeImpl(dnaEvaluator(), evaluationsService, modelMapper());
        }
    }

}