package me.mrs.mutantes.servicios.domain;

import me.mrs.mutantes.servicios.DnaConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DnaValidator implements ConstraintValidator<DnaConstraint, List<String>> {

    public static final String VALID_SYMBOLS = "^[ATGC]+$";

    private static boolean allSymbolsValid(String sequence) {
        return sequence.matches(VALID_SYMBOLS);
    }

    @Override
    public boolean isValid(
            List<String> dna, ConstraintValidatorContext constraintValidatorContext) {
        if (dna == null || dna.isEmpty() || dna.stream().anyMatch(Objects::isNull)) return false;

        Set<Integer> sizeSet = dna.stream().map(String::length).collect(Collectors.toSet());
        if (sizeSet.size() != 1 || !sizeSet.contains(dna.size())) return false;

        return dna.stream().allMatch(DnaValidator::allSymbolsValid);
    }
}
