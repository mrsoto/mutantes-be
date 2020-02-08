package me.mrs.mutantes.servicios;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.stream.Collectors;

public class DnaValidator implements ConstraintValidator<DnaConstraint, Collection<String>> {

    public static final String VALID_SYMBOLS = "^[ATGC]+$";

    @Override
    public boolean isValid(Collection<String> dna,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dna == null || dna.isEmpty() || dna.contains(null)) return false;

        if (dna.stream().map(String::length).collect(Collectors.toSet()).size() != 1) return false;

        return dna.stream().allMatch(sequence -> sequence.matches(VALID_SYMBOLS));
    }
}
