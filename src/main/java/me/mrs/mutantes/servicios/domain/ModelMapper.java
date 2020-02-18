package me.mrs.mutantes.servicios.domain;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ModelMapper {
    @NonNull
    public EvaluationModel toBusinessModel(
            List<String> dna, boolean mutant) {
        return new EvaluationModel(dna, mutant, Instant.now());
    }

    @NonNull
    public StatsViewModel toViewModel(@NonNull StatsModel stats) {
        return new StatsViewModel(stats.getHumans(), stats.getMutants());
    }
}
