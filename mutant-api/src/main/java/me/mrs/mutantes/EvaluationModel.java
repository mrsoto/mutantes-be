package me.mrs.mutantes;

import java.time.Instant;
import java.util.List;

public interface EvaluationModel {
    List<String> getDna();

    boolean isMutant();

    Instant getTimestamp();
}
