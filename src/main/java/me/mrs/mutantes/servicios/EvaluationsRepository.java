package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.domain.EvaluationModel;
import me.mrs.mutantes.servicios.domain.StatsModel;

import java.util.List;

public interface EvaluationsRepository {
    int getSupportedBatchSize();

    void batchInsert(List<EvaluationModel> evaluations);

    StatsModel getStats();
}
