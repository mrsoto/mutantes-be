package me.mrs.mutantes.persistence;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.EvaluationsRepository;
import me.mrs.mutantes.StatsModel;
import me.mrs.mutantes.annotaion.InsertBatchSize;
import me.mrs.mutantes.entity.StatsModelEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collection;
import java.util.Objects;

public class HibernateEvaluationsRepository implements EvaluationsRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int batchSize;
    private final JPATemplate jpaTemplate;
    private EntityManagerFactory entityManagerFactory;

    @Inject
    public HibernateEvaluationsRepository(
            @InsertBatchSize int batchSize, @NotNull EntityManagerFactory entityManagerFactory) {
        this.batchSize = batchSize;
        this.entityManagerFactory = entityManagerFactory;
        this.jpaTemplate = new JPATemplate(entityManagerFactory);
    }

    private static void updateStats(EntityManager entityManager, long mutants, int humans) {
        var query = entityManager
                .createNamedQuery(StatsModelEntity.UPDATE_STATS_QUERY)
                .setParameter("mutants", mutants)
                .setParameter("humans", humans);

        query.executeUpdate();
    }

    @Override
    public int getSupportedBatchSize() {
        return batchSize;
    }

    @Override
    public void batchInsert(@NotNull Collection<EvaluationModel> evaluations) {
        jpaTemplate.doInJPATransaction(entityManager -> {
            final var humans = evaluations.size();
            final var mutants = insertEvaluations(entityManager, evaluations);
            updateStats(entityManager, mutants, humans);

            logger.debug("Inserting {} inquires. Humans:{} Mutants:{}",
                    evaluations.size(),
                    humans,
                    mutants);
        });
    }

    private int insertEvaluations(
            @NotNull EntityManager entityManager, @NotNull Iterable<EvaluationModel> evaluations) {
        var count = 0;
        var mutants = 0;
        for (var evaluation : evaluations) {
            entityManager.persist(evaluation);
            if (evaluation.isMutant()) {
                mutants++;
            }
            count++;
            if (count >= batchSize) {
                entityManager.flush();
                entityManager.clear();
                count = 0;
            }
        }

        return mutants;
    }

    @Override
    @NotNull
    public StatsModel getStats() {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            StatsModel stats = entityManager
                    .createNamedQuery(StatsModelEntity.ALL_STATS_QUERY, StatsModelEntity.class)
                    .getSingleResult();
            return Objects.requireNonNull(stats);
        } finally {
            entityManager.close();
        }
    }
}
