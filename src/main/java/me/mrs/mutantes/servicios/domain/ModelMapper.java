package me.mrs.mutantes.servicios.domain;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ModelMapper {
    public EvaluationModel toBusinessModel(@NonNull DnaViewModel model, boolean mutant) {
        return new EvaluationModel(model.getDna(), mutant, Instant.now());
    }

    public StatsViewModel toViewModel(@NonNull StatsModel stats) {
        return new StatsViewModel(stats.getHumans(), stats.getMutants());
    }
}
