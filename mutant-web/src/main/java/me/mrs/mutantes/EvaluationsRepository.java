package me.mrs.mutantes;

import java.util.Collection;
import java.util.Optional;

public interface EvaluationsRepository {
    int getSupportedBatchSize();

    void batchInsert(Collection<EvaluationModel> evaluations);

    Optional<StatsModel> getStats();
}
