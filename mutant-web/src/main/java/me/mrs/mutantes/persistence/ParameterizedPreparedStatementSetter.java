package me.mrs.mutantes.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParameterizedPreparedStatementSetter<T> {
    void accept(PreparedStatement statement, T row) throws SQLException;
}
