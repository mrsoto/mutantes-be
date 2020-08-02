package me.mrs.mutantes;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Null;

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
    public Double getRatio() {
        if (humans == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) mutants / (double) humans;
    }
}
