package me.mrs.mutantes.servicios.domain;

import me.mrs.mutantes.servicios.DnaConstraint;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@Validated
public class DnaViewModel {
    @NonNull
    @DnaConstraint
    private final List<String> dna;

    public DnaViewModel(@NonNull List<String> dna) {
        this.dna = dna;
    }

    public DnaViewModel() {
        this(Collections.emptyList());
    }

    @NonNull
    public List<String> getDna() {
        return Collections.unmodifiableList(dna);
    }
}
