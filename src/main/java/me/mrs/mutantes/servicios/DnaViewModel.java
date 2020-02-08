package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;

@Validated
public class DnaViewModel {
    @NonNull
    private Collection<String> dna;

    public DnaViewModel(@NonNull Collection<String> dna) {
        this.dna = dna;
    }

    public DnaViewModel() {
        this(Collections.emptyList());
    }

    @NonNull
    public Collection<String> getDna() {
        return Collections.unmodifiableCollection(dna);
    }

    @Override
    public String toString() {
        return "DnaViewModel{" + "dna=" + dna + '}';
    }
}
