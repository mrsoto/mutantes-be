package me.mrs.mutantes.entity;

import me.mrs.mutantes.StatsModel;

public final class StatsModelEntity implements StatsModel {
    private final long mutants;
    private final long humans;

    public StatsModelEntity(long humans, long mutants) {
        this.mutants = mutants;
        this.humans = humans;
    }

    @Override
    public long getMutants() {
        return mutants;
    }

    @Override
    public long getHumans() {
        return humans;
    }

    @Override
    public Double getRatio() {
        return (double) humans / (double) mutants;
    }
}
