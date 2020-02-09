package me.mrs.mutantes.servicios;

import liquibase.integration.spring.SpringLiquibase;
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
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("GIVEN a Evaluations Repository")
class EvaluationsRepositoryImplTest {

    @Autowired
    EvaluationsRepositoryImpl target;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("SHOULD insert batch queries into the database")
    void batchInsert() {
        Instant now = Instant.now();
        Collection<QueryModel> quieries = List.of(new QueryModel[]{new QueryModel("AAAA",
                true,
                now), new QueryModel("AACC,ACGT", false, now),});

        target.batchInsert(quieries);

        int insertCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "dna");
        var humanCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "dna",
                "sequence = 'AACC,ACGT' and mutant='f'");
        var mutantCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "dna",
                "sequence = 'AAAA' and mutant='t'");

        Assertions.assertAll("After all Queries are inserted",
                () -> assertEquals(2, insertCount),
                () -> assertEquals(1, humanCount),
                () -> assertEquals(1, mutantCount));
    }

    @Configuration
    static class LocalConfig {
        @Value("${application.queries.insert.batchSize}")
        int batchSize;

        @Value("${application.queries.insert.statement}")
        String insertStatement;

        @Bean
        ParametrizedDnaInsertSetter parametrizedDnaInsertSetter() {
            return new ParametrizedDnaInsertSetter();
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
        EvaluationsRepositoryImpl evaluationsRepository() {
            return new EvaluationsRepositoryImpl(jdbcTemplate(),
                    batchSize,
                    insertStatement,
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