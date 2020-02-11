package me.mrs.mutantes.servicios.persistence;

import liquibase.integration.spring.SpringLiquibase;
import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.StatsModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("GIVEN a Evaluations Repository")
class JdbcEvaluationsRepositoryTest {

    @Autowired
    JdbcEvaluationsRepository target;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    LocalConfig localConfig;

    @Test
    @DisplayName("SHOULD insert batch queries into the database")
    void batchInsert() {
        var now = Instant.now();
        var mod1 = new EvaluationModel(List.of("AAAA"), true, now);
        var mod2 = new EvaluationModel(List.of("AACC", "ACGT"), false, now);
        var mod3 = new EvaluationModel(List.of("AACC"), false, now);
        var queries = List.of(new EvaluationModel[]{mod1, mod2, mod3,});

        target.batchInsert(queries);

        int insertCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "dna");
        var humanCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "dna",
                "sequence in ( 'AACC,ACGT','AACC') and mutant='f'");
        var mutantCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "dna",
                "sequence = 'AAAA' and mutant='t'");

        var statsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "summary");
        StatsModel stats = target.getStats();

        assertAll("After all Queries are inserted",
                () -> assertEquals(3, insertCount),
                () -> assertEquals(2, humanCount),
                () -> assertEquals(1, mutantCount));

        assertAll("After stats ware updated",
                () -> assertEquals(1, statsCount),
                () -> assertEquals(3, stats.getHumans()),
                () -> assertEquals(1, stats.getMutants()),
                () -> assertTrue(1d / 3d - stats.getRatio() < 1E-6));
    }

    @Test
    void getSupportedBatchSize() {
        Assertions.assertEquals(localConfig.batchSize, target.getSupportedBatchSize());
    }

    @Configuration
    static class LocalConfig {
        @Value("${application.queries.insert.batchSize}")
        int batchSize;

        @Value("${application.queries.insert.statement}")
        String insertStatement;
        @Value("${application.stats.update.statement}")
        private String updateStatsStatement;
        @Value("${application.stats.query.statement}")
        private String queryStatsStatement;

        @Bean
        DnaPrepareInsertStatementSetter parametrizedDnaInsertSetter() {
            return new DnaPrepareInsertStatementSetter(false);
        }

        @Bean
        DataSource dataSource() {
            var builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(dataSource());
        }

        @Bean
        JdbcEvaluationsRepository evaluationsRepository() {
            return new JdbcEvaluationsRepository(jdbcTemplate(),
                    batchSize,
                    insertStatement,
                    updateStatsStatement,
                    queryStatsStatement,
                    parametrizedDnaInsertSetter());
        }

        @Bean
        SpringLiquibase liquibase() {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource());
            liquibase.isDropFirst();
            liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.yaml");
            return liquibase;
        }

    }
}