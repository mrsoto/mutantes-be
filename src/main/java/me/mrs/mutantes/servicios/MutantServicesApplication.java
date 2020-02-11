package me.mrs.mutantes.servicios;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@EnableCaching
@SpringBootApplication
public class MutantServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutantServicesApplication.class, args);
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource(@Value("${spring.datasource.url}") String dbUrl) {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }

    @Bean
    SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.isDropFirst();
        liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.yaml");
        return liquibase;
    }

}
