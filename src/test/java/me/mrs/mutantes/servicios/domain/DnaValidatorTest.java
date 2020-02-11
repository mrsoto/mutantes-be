package me.mrs.mutantes.servicios.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("GIVEN a DnaValidatorTest")
class DnaValidatorTest {

    @Mock
    ConstraintValidatorContext validatorContext;

    static Stream<List<String>> isInvalid() {
        return Stream.of(List.of(),
                List.of("X"),
                List.of("AAA", "CCCC"),
                Arrays.asList("AAA", "CCC", null));
    }

    @Test
    @DisplayName("WHEN the DNA is Valid THEN should return True")
    void isValid() {
        var validator = new DnaValidator();
        assertTrue(validator.isValid(List.of("AAAA", "CCCC", "GGGG", "TTTT"), validatorContext));
    }

    @DisplayName("WHEN the DNA is Valid THEN should return True")
    @ParameterizedTest()
    @MethodSource
    void isInvalid(List<String> dna) {
        var validator = new DnaValidator();
        assertFalse(validator.isValid(dna, validatorContext));
    }
}