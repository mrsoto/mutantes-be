package me.mrs.mutantes;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DnaViewModel {
    @Nonnull
    @DnaConstraint
    private final List<String> dna;

    public DnaViewModel(@Nonnull List<String> dna) {
        this.dna = ImmutableList.copyOf(dna);
    }

    public DnaViewModel() {
        this(Collections.emptyList());
    }

    @Nonnull
    public List<String> getDna() {
        return ImmutableList.copyOf(dna);
    }
}
