package me.mrs.mutantes.persistence;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.EvaluationsRepository;
import me.mrs.mutantes.StatsModel;
import me.mrs.mutantes.annotaion.DnaConverter;
import me.mrs.mutantes.annotaion.InsertBatchSize;
import me.mrs.mutantes.converter.AttributeConverter;
import me.mrs.mutantes.entity.StatsModelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Singleton
public class JdbcEvaluationsRepository implements EvaluationsRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final int batchSize;
    private final ParameterizedPreparedStatementSetter<EvaluationModel> parametrizedInsertSetter;
    private final ParameterizedPreparedStatementSetter<StatsModel> parametrizedUpdateSetter;
    private final ResultRowMapper<StatsModel> parametrizedStatsMapper;

    @Inject
    public JdbcEvaluationsRepository(
            @Nonnull JdbcTemplate jdbcTemplate,
            @InsertBatchSize int batchSize,
            @DnaConverter @Nonnull AttributeConverter<List<String>, String> dnaConverter
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.batchSize = batchSize;
        this.parametrizedInsertSetter = new EvaluationInsertStatement(dnaConverter);
        this.parametrizedUpdateSetter = new StatsUpdateStatement();
        this.parametrizedStatsMapper = new StatsQueryStatement();
    }

    @Override
    public int getSupportedBatchSize() {
        return batchSize;
    }

    @Override
    public void batchInsert(Collection<EvaluationModel> evaluations) {
        long mutants = evaluations.stream().filter(EvaluationModel::isMutant).count();
        int humans = evaluations.size();

        jdbcTemplate.doInTransaction(connection -> {
            jdbcTemplate.batchStatement(connection,
                    EvaluationInsertStatement.SQL,
                    evaluations,
                    parametrizedInsertSetter);
            final StatsModel statsModel = new StatsModelEntity(humans, mutants);
            jdbcTemplate.update(connection,
                    StatsUpdateStatement.SQL,
                    statsModel,
                    parametrizedUpdateSetter);
        });

        logger.debug("Inserting {} inquires. Humans:{} Mutants:{}",
                evaluations.size(),
                humans,
                mutants);
    }

    @Override
    public Optional<StatsModel> getStats() {
        return jdbcTemplate.queryForObject(StatsQueryStatement.SQL, this.parametrizedStatsMapper);
    }

}
