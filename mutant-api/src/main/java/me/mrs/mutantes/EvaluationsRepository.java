package me.mrs.mutantes;

import java.util.Collection;

public interface EvaluationsRepository {
    int getSupportedBatchSize();

    void batchInsert(Collection<EvaluationModel> evaluations);

    StatsModel getStats();
}
