package me.mrs.mutantes.servicios.persistence;

import me.mrs.mutantes.servicios.EvaluationsRepository;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.StatsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcEvaluationsRepository implements EvaluationsRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final int batchSize;
    private final String insertStatement;
    private final String updateStatsStatement;
    private final String queryStatsStatement;
    private final ParameterizedPreparedStatementSetter<EvaluationModel> parametrizedInsertSetter;
    private final NamedParameterJdbcTemplate namedParametersJdbcTemplate;

    public JdbcEvaluationsRepository(
            JdbcTemplate jdbcTemplate,
            @Value("${application.queries.insert.batchSize}") int batchSize,
            @Value("${application.queries.insert.statement}") String insertStatement,
            @Value("${application.stats.update.statement}") String updateStatsStatement,
            @Value("${application.stats.query.statement}") String queryStatsStatement,
            ParameterizedPreparedStatementSetter<EvaluationModel> parametrizedDnaInsertSetter) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParametersJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.batchSize = batchSize;
        this.insertStatement = insertStatement;
        this.updateStatsStatement = updateStatsStatement;
        this.queryStatsStatement = queryStatsStatement;
        this.parametrizedInsertSetter = parametrizedDnaInsertSetter;
    }

    @Override
    public int getSupportedBatchSize() {
        return batchSize;
    }

    @Override
    @Transactional
    public void batchInsert(List<EvaluationModel> evaluations) {
        long mutants = evaluations.stream().filter(EvaluationModel::isMutant).count();
        int humans = evaluations.size();
        MapSqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("mutants", mutants)
                .addValue("humans", humans);

        logger.debug("Inserting {} inquires. Humans:{} Mutants:{}",
                evaluations.size(),
                humans,
                mutants);

        jdbcTemplate.batchUpdate(insertStatement, evaluations, batchSize, parametrizedInsertSetter);

        var updates = namedParametersJdbcTemplate.update(updateStatsStatement, sqlParameters);

        if (updates == 0) {
            logger.error("Unable to update stats");
        }
    }

    @Override
    public StatsModel getStats() {
        return jdbcTemplate.queryForObject(queryStatsStatement, this::statsRowMapper);
    }

    private StatsModel statsRowMapper(ResultSet resultSet, int i) throws SQLException {
        long humans = resultSet.getLong(1);
        long mutants = resultSet.getLong(2);
        return new StatsModel(humans, mutants);
    }
}
