package me.mrs.mutantes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

public final class StatsViewModel {

    @JsonProperty("count_human_dna")
    private final long humans;

    @JsonProperty("count_mutant_dna")
    private final long mutants;

    public StatsViewModel(long humans, long mutants) {
        this.mutants = mutants;
        this.humans = humans;
    }

    public long getHumans() {
        return humans;
    }

    public long getMutants() {
        return mutants;
    }

    @JsonProperty("ratio")
    public @Nullable
    Double getRatio() {
        if (humans == 0) {
            return null;
        }
        return (double) mutants / (double) humans;
    }
}
