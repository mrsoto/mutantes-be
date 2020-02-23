package me.mrs.mutantes.entity;

import me.mrs.mutantes.StatsModel;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "stats")
@Table(name = "stats")
@NamedQuery(name = StatsModelEntity.UPDATE_STATS_QUERY, query =
        "UPDATE summary SET (mutants, " + "humans) = (mutants + :mutants, humans + :humans)")
@NamedQuery(name = StatsModelEntity.ALL_STATS_QUERY, query = "select s from stats s")
public final class StatsModelEntity implements StatsModel {
    public static final String ALL_STATS_QUERY = "allStats";
    public static final String UPDATE_STATS_QUERY = "updateStats";
    private final long mutants;

    private final long humans;

    public StatsModelEntity() {
        mutants = 0;
        humans = 0;
    }

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
