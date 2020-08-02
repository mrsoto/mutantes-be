package me.mrs.mutantes.persistence;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.sql.Statement.EXECUTE_FAILED;

@Singleton
public class JdbcTemplate {
    @Nonnull
    private final DataSource dataSource;

    @Inject
    public JdbcTemplate(@Nonnull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void doInTransaction(ThrowableConsumer<Connection> task) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            task.accept(connection);
//            Thread.sleep(3,0);
            connection.commit();
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Throwable e) {
            throw new RollBackException(e);
        }
    }

    public <T> void batchStatement(
            Connection connection,
            String sentence,
            Iterable<T> rows,
            ParameterizedPreparedStatementSetter<T> parametrizedInsertSetter) throws SQLException {
        batchStatement(connection, sentence, rows, parametrizedInsertSetter, Integer.MAX_VALUE);
    }

    public <T> void batchStatement(
            Connection connection,
            String sentence,
            Iterable<T> rows,
            ParameterizedPreparedStatementSetter<T> parametrizedInsertSetter,
            int batchSize) throws SQLException {
        int rowCount = 0;
        try (var statement = connection.prepareStatement(sentence)) {
            for (var row : rows) {
                parametrizedInsertSetter.accept(statement, row);
                statement.addBatch();
                rowCount++;
                if (rowCount > batchSize) {
                    flush(sentence, statement.executeBatch());
                    rowCount = 0;
                }
            }
            var updateCounts = statement.executeBatch();
            flush(sentence, updateCounts);
        }
    }

    public void flush(String sentence, int[] ints) {
        var anyFail = IntStream.of(ints).anyMatch(i -> i == EXECUTE_FAILED);
        if (anyFail) {
            throw new SqlUpdateFailureException(String.format(
                    "Unable to execute sentence. One or more records fails: %s",
                    sentence));
        }
    }

    public <T> void update(
            @Nonnull Connection connection,
            @Nonnull String sentence,
            @Nonnull T model,
            ParameterizedPreparedStatementSetter<T> parametrizedSetter
    ) throws SQLException {
        try (var statement = connection.prepareStatement(sentence)) {
            parametrizedSetter.accept(statement, model);
            var fail = statement.execute();
            if (fail) {
                throw new SqlUpdateFailureException(String.format("Unable to execute sentence: %s",
                        sentence
                ));
            }
        }
    }

    @Nonnull
    public <T> Optional<T> queryForObject(
            @Nonnull String sentence, @Nonnull ResultRowMapper<T> resultRowMapper
    ) {
        try (var connection = dataSource.getConnection()) {
            return queryForObject(connection, sentence, resultRowMapper);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    @Nonnull
    public <T> Optional<T> queryForObject(
            @Nonnull Connection connection,
            @Nonnull String sentence,
            @Nonnull ResultRowMapper<T> resultRowMapper
    ) {
        try (var statement = connection.prepareStatement(sentence)) {
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var model = resultRowMapper.mapRow(resultSet);
                    return Optional.of(model);
                } else {
                    return Optional.empty();
                }
            }
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new QueryException(e);
        }
    }

    @FunctionalInterface
    public interface ThrowableConsumer<T> {
        @SuppressWarnings("squid:S00112")
        void accept(@Nonnull T value) throws Throwable;
    }

    public static class RollBackException extends RuntimeException {
        public RollBackException(@Nonnull Throwable e) {
            super(e);
        }
    }

    private static class SqlUpdateFailureException extends RuntimeException {
        public SqlUpdateFailureException(@Nonnull String cause) {
            super(cause);
        }
    }

    private static class QueryException extends RuntimeException {
        public QueryException(Throwable e) {
            super(e);
        }
    }
}
