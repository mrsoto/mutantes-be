package me.mrs.mutantes.persistence;

import me.mrs.mutantes.StatsModel;
import me.mrs.mutantes.entity.StatsModelEntity;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class StatsQueryStatement implements ResultRowMapper<StatsModel> {
    public static final String SQL = "SELECT humans, mutants FROM summary";

    @NotNull
    @Override
    public StatsModel mapRow(@NotNull ResultSet resultSet) throws SQLException {
        var humans = resultSet.getLong(1);
        var mutants = resultSet.getLong(2);
        return new StatsModelEntity(humans, mutants);
    }
}
