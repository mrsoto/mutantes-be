package me.mrs.mutantes.servicios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
        void isMutant(String dna) {
            var target = new DnaEvaluatorImpl();

            assertFalse(target.isMutant(List.of(dna)));
        }

        @DisplayName("AND 4 or more repeated sequences exist THEN should be a Mutant")
        @ParameterizedTest(name = "Mutant dna={0}")
        @MethodSource
        void isMutantSingleRow(String dna) {
            var target = new DnaEvaluatorImpl();

            assertTrue(target.isMutant(List.of(dna)));
        }

        Stream<String> isMutantSingleRow() {
            var patterns = Stream.of("*", "X*", "*X", "X*X");
            var symbols = List.of("A", "C", "G", "T");
            return patterns.flatMap(pattern -> symbols
                    .stream()
                    .map(symbol -> pattern.replace("*", symbol.repeat(4))));
        }

    }

    @Nested
    @TestInstance(PER_CLASS)
    @DisplayName("WHEN a multiples rows are evaluated")
    class MultipleRows {

        @DisplayName("AND no 4 sequences exist THEN should be a Human")
        @Test
        void isMutant() {
            var target = new DnaEvaluatorImpl();
            var dna = List.of("ACGT", "CAGT", "GTAC", "TGACT");

            assertFalse(target.isMutant(dna));
        }

        @DisplayName("AND 4 or more repeated sequences exist THEN should be a Mutant")
        @ParameterizedTest(name = "Mutant dna={0}")
        @MethodSource
        void isMutantSingleCol(List<String> dna) {
            var target = new DnaEvaluatorImpl();

            assertTrue(target.isMutant(dna));
        }

        Stream<List<String>> isMutantSingleCol() {

            // @formatter:off
            return Stream.of(
                    List.of(
                            "A123",
                            "A456",
                            "A123",
                            "A456"),
                    List.of(
                            "1A23",
                            "4A56",
                            "1A23",
                            "4A56"),
                    List.of(
                            "123A",
                            "456A",
                            "123A",
                            "456A"),
                    List.of(
                            "G1234C",
                            "G****C",
                            "G1234C",
                            "G4321C"),
                    List.of(
                            "*1234C",
                            "G*123C",
                            "G1*34C",
                            "G43*1G"),
                    List.of(
                            "A1234*",
                            "GA12*C",
                            "G12*4C",
                            "G4*A1G")
                    );
            // @formatter:on
        }
    }
}