package me.mrs.mutantes.servicios;

import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class ParametrizedDnaInsertSetter implements ParameterizedPreparedStatementSetter<QueryModel> {
    @Override
    public void setValues(
            PreparedStatement preparedStatement, QueryModel queryModel) throws SQLException {
        preparedStatement.setTimestamp(1, Timestamp.from(queryModel.getInstant()));
        preparedStatement.setClob(2, new StringReader(queryModel.getDna()));
        preparedStatement.setBoolean(3, queryModel.getMutant());

    }
}
