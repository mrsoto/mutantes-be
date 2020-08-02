package me.mrs.mutantes.persistence;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.converter.AttributeConverter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Singleton
public class EvaluationInsertStatement implements ParameterizedPreparedStatementSetter<EvaluationModel> {
    public static final String SQL =
            "INSERT INTO evaluations(instant,sequence,mutant) values(?," + "?,?)";
    private final AttributeConverter<List<String>, String> dnaConverter;

    @Inject
    public EvaluationInsertStatement(AttributeConverter<List<String>, String> dnaConverter) {
        this.dnaConverter = dnaConverter;
    }

    @Override
    public void accept(PreparedStatement statement, EvaluationModel row) throws SQLException {
        statement.setTimestamp(1, Timestamp.from(row.getTimestamp()));
        statement.setString(2, dnaConverter.convertToDatabaseColumn(row.getDna()));
        statement.setBoolean(3, row.isMutant());
    }
}
