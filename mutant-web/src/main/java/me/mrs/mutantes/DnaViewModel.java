package me.mrs.mutantes;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DnaViewModel {
    @NotNull
    @DnaConstraint
    private final List<String> dna;

    public DnaViewModel(@NotNull List<String> dna) {
        this.dna = ImmutableList.copyOf(dna);
    }

    public DnaViewModel() {
        this(Collections.emptyList());
    }

    @NotNull
    public List<String> getDna() {
        return ImmutableList.copyOf(dna);
    }
}
