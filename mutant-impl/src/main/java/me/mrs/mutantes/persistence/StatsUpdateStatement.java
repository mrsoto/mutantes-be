package me.mrs.mutantes.persistence;

import me.mrs.mutantes.StatsModel;

import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
public class StatsUpdateStatement implements ParameterizedPreparedStatementSetter<StatsModel> {
    public static final String SQL = "UPDATE summary SET (mutants, humans) = (mutants + ?, " +
            "humans" + " + ?)";

    @Override
    public void accept(PreparedStatement statement, StatsModel row) throws SQLException {
        statement.setLong(1, row.getMutants());
        statement.setLong(2, row.getHumans());
    }
}
