package me.mrs.mutantes.persistence;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;

public class JPATemplate {
    private final Logger logger = LoggerFactory.getLogger(JPATemplate.class);
    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public JPATemplate(@NotNull EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void doInJPATransaction(@NotNull Consumer<EntityManager> callable) {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            callable.accept(entityManager);
            entityManager.flush();
            entityManager.clear();
            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.error("Error inserting evaluations", e);
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
    }
}
