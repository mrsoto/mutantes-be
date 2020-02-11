package me.mrs.mutantes.servicios.domain;

public final class StatsModel {
    private final long mutants;

    private final long humans;

    public StatsModel(long humans, long mutants) {
        this.mutants = mutants;
        this.humans = humans;
    }

    public long getMutants() {
        return mutants;
    }

    public long getHumans() {
        return humans;
    }

    public double getRatio() {
        return (double) humans / (double) mutants;
    }
}
