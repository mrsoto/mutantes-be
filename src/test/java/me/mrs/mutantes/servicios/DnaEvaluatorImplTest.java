package me.mrs.mutantes.servicios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@DisplayName("GIVEN a DNA Evaluator")
class DnaEvaluatorImplTest {

    @Nested
    @TestInstance(PER_CLASS)
    @DisplayName("WHEN a single row is evaluated")
    class SingleRow {

        @DisplayName("AND no 4 or more repeated sequences exist THEN should be a Human")
        @ParameterizedTest(name = "Human dna={0}")
        @ValueSource(strings = {"A", "ATGC", "AAAT", "AAATA"})
        void isHuman(String dna) {
            var target = new DnaEvaluatorImpl();

            assertTrue(target.isHuman(List.of(dna)));
        }

        @DisplayName("AND 4 or more repeated sequences exist THEN should be a Mutant")
        @ParameterizedTest(name = "Mutant dna={0}")
        @MethodSource
        void isMutantSingleRow(String dna) {
            var target = new DnaEvaluatorImpl();

            assertFalse(target.isHuman(List.of(dna)));
        }

        Stream<String> isMutantSingleRow() {
            var patterns = Stream.of("*", "X*", "*X", "X*X");
            var symbols = List.of("A", "C", "G", "T");
            return patterns.flatMap(pattern -> symbols
                    .stream()
                    .map(symbol -> pattern.replace("*", symbol.repeat(4))));
        }
    }

}