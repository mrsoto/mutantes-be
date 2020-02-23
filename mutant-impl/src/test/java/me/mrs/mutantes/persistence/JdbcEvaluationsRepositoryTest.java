package me.mrs.mutantes.persistence;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.entity.EvaluationModelEntity;
import me.mrs.mutantes.entity.StatsModelEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GIVEN a Evaluations Repository")
@ExtendWith({MockitoExtension.class})
class JdbcEvaluationsRepositoryTest {

    HibernateEvaluationsRepository target;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private Query updateStatsQuery;

    @BeforeEach
    void setup() {
        target = new HibernateEvaluationsRepository(10, entityManagerFactory);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createNamedQuery(eq(StatsModelEntity.UPDATE_STATS_QUERY))).thenReturn(
                updateStatsQuery);
        when(updateStatsQuery.setParameter(anyString(), any())).thenReturn(updateStatsQuery);
    }

    @Test
    @DisplayName("SHOULD insert batch queries into the database")
    void batchInsert() {
        var now = Instant.now();
        var mod1 = new EvaluationModelEntity(List.of("AAAA"), true, now);
        var mod2 = new EvaluationModelEntity(List.of("AACC", "ACGT"), false, now);
        var mod3 = new EvaluationModelEntity(List.of("AACC"), false, now);
        Collection<EvaluationModel> queries = List.of(mod1, mod2, mod3);

        target.batchInsert(queries);

        verify(transaction).begin();
        for (var entity : queries) {
            verify(entityManager).persist(entity);
        }
        verify(entityManager).createNamedQuery(StatsModelEntity.UPDATE_STATS_QUERY);
        verify(updateStatsQuery).setParameter("mutants", 1L);
        verify(updateStatsQuery).setParameter("humans", 3);
        verify(updateStatsQuery).executeUpdate();
        verify(entityManager).flush();
        verify(transaction).commit();
    }

}