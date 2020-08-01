package me.mrs.mutantes.servicios.persistence;

import me.mrs.mutantes.servicios.domain.EvaluationModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class DnaPrepareInsertStatementSetter implements ParameterizedPreparedStatementSetter<EvaluationModel> {
    private final boolean insertClob;

    public DnaPrepareInsertStatementSetter(
            @Value("${application.queries.insert.clob:#{false}}") boolean insertClob) {
        this.insertClob = insertClob;
    }

    @Override
    public void setValues(
            PreparedStatement preparedStatement, EvaluationModel queryModel) throws SQLException {
        preparedStatement.setTimestamp(1, Timestamp.from(queryModel.getTimestamp()));

        String serializedDna = serialize(queryModel.getDna());
        if (insertClob) {
            preparedStatement.setClob(2, new StringReader(serializedDna));
        } else {
            preparedStatement.setString(2, serializedDna);
        }

        preparedStatement.setBoolean(3, queryModel.isMutant());

    }

    private static String serialize(List<String> dna) {
        return String.join(",", dna);
    }

}
