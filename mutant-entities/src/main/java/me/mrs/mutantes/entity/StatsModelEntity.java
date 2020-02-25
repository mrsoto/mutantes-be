package me.mrs.mutantes.entity;

import me.mrs.mutantes.StatsModel;

import javax.persistence.*;

@Entity(name = "summary")
@Table(name = "summary")
@SqlResultSetMapping(name = "updateResult", columns = {@ColumnResult(name = "count")})
@NamedNativeQuery(name = StatsModelEntity.UPDATE_STATS_QUERY, query = "UPDATE summary SET " +
        "(mutants, " + "humans) = (mutants + :mutants, humans + :humans)", resultSetMapping =
        "updateResult")
@NamedQuery(name = StatsModelEntity.ALL_STATS_QUERY, query = "select s from summary s")
public final class StatsModelEntity implements StatsModel {
    public static final String ALL_STATS_QUERY = "allStats";
    public static final String UPDATE_STATS_QUERY = "updateStats";

    @Id
    private long id;
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
