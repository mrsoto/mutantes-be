package me.mrs.mutantes.servicios;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DnaValidator implements ConstraintValidator<DnaConstraint, Collection<String>> {

    public static final String VALID_SYMBOLS = "^[ATGC]+$";

    private static boolean allSymbolsValid(String sequence) {
        return sequence.matches(VALID_SYMBOLS);
    }

    @Override
    public boolean isValid(Collection<String> dna,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dna == null || dna.isEmpty() || dna.contains(null)) return false;

        Set<Integer> sizeSet = dna.stream().map(String::length).collect(Collectors.toSet());
        if (sizeSet.size() != 1) return false;

        return dna.stream().allMatch(DnaValidator::allSymbolsValid);
    }
}
