package me.mrs.mutantes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class DnaValidator implements ConstraintValidator<DnaConstraint, List<String>> {

    public static final String VALID_SYMBOLS = "^[ATGC]+$";

    private static boolean allSymbolsValid(String sequence) {
        return sequence.matches(VALID_SYMBOLS);
    }

    @Override
    public boolean isValid(
            @Nullable List<String> dna,
            @Nonnull ConstraintValidatorContext constraintValidatorContext
    ) {
        if (dna == null || dna.isEmpty() || dna.stream().anyMatch(Objects::isNull)) {
            return false;
        }

        var sizeSet = dna.stream().map(String::length).collect(Collectors.toSet());
        if (sizeSet.size() != 1 || !sizeSet.contains(dna.size())) {
            return false;
        }

        return dna.stream().allMatch(DnaValidator::allSymbolsValid);
    }
}
