package me.mrs.mutantes.servicios;

import java.util.Collection;

public interface EvaluationsRepository {
    int[][] batchInsert(Collection<QueryModel> queries);
}
