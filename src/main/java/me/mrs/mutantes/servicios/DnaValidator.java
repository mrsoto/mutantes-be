package me.mrs.mutantes.servicios;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Set;

public class DnaValidator implements ConstraintValidator<DnaConstraint, Collection<String>> {
    @Override
    public boolean isValid(Collection<String> dna, ConstraintValidatorContext constraintValidatorContext) {
        // TODO: Implement validator
        var invalid = Set.of("A.TAGTGC", "CTGCGA.null.ATCTGT", "XAAAA.AAAAA");
        return !invalid.contains(String.join(".", dna));
    }
}
