package me.mrs.mutantes.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class EvaluationsRepositoryImpl implements EvaluationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final int batchSize;
    private String insertStatement;
    private ParameterizedPreparedStatementSetter<QueryModel> parametrizedInsertSetter;

    public EvaluationsRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            @Value("${application.queries.insert.batchSize}") int batchSize,
            @Value("${application.queries.insert.statement}") String insertStatement,
            ParameterizedPreparedStatementSetter<QueryModel> parametrizedDnaInsertSetter) {
        this.jdbcTemplate = jdbcTemplate;
        this.batchSize = batchSize;
        this.insertStatement = insertStatement;
        this.parametrizedInsertSetter = parametrizedDnaInsertSetter;
    }

    @Override
    public int[][] batchInsert(Collection<QueryModel> queries) {
        return jdbcTemplate.batchUpdate(insertStatement,
                queries,
                batchSize,
                parametrizedInsertSetter);
    }
}
