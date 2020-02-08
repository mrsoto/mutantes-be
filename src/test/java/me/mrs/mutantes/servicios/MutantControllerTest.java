package me.mrs.mutantes.servicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("GIVEN a Human Controller")
class MutantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    static private Stream<Collection<String>> mutantSamples() {
        List<String> horizontalC = Arrays.asList("ATGAGT", "TAGTGC", "ATATGT", "AGAAGG", "CCCCTA", "TCATTG");
        List<String> diagonalLetterC = Arrays.asList("CTGCGA", "ACATGC", "ATCTGT", "AGACGA", "GATGCA", "TCACTG");
        List<String> verticalLetterC = Arrays.asList("TTGCGA", "AAGTCC", "ATATCT", "AGAACG", "CTATCA", "TCACTG");
        return Stream.of(horizontalC, diagonalLetterC, verticalLetterC);
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
        var requestPayload = Collections.singletonMap("dna",
                Arrays.asList("ATGCGA", "CAGTAC", "TTATGT"));

        mockMvc
                .perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("mutant"));
    }

    @DisplayName("WHEN a Mutant is inquired")
    @ParameterizedTest(name = "AND the adn is like {0} THEN should response HTTP_STATUS.Forbidden")
    @MethodSource("mutantSamples")
    public void whenAMutantIsInquiredThenShouldResponseForbidden(Collection<String> dna) throws Exception {
        var objectMapper = new ObjectMapper();
        var requestPayload = Collections.singletonMap("dna", dna);

        mockMvc
                .perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andDo(document("mutant"));
    }

    @DisplayName("WHEN a DNA is inquired")
    @ParameterizedTest(name = "AND is an invalid DNA sequence like {0} THEN should response HTTP_STATUS.BadRequest")
    @MethodSource("invalidDnaSamples")
    public void whenInvalidDndIsInquired(Collection<String> dna) throws Exception {
        var objectMapper = new ObjectMapper();
        var requestPayload = Collections.singletonMap("dna", dna);

        mockMvc
                .perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestPayload)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("mutant"));

    }

    @TestConfiguration
    static class Config {
        @Bean
        DnaEvaluator evaluatorService() {
            return new DnaEvaluatorImpl();
        }
    }

}