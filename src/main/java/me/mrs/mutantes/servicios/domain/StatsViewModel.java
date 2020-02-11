package me.mrs.mutantes.servicios.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        return humans != 0 ? (double) mutants / (double) humans : null;
    }
}
