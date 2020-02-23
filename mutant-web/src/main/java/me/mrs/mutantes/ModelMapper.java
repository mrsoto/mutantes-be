package me.mrs.mutantes;

import me.mrs.mutantes.entity.EvaluationModelEntity;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import java.time.Instant;

public class ModelMapper {
    public EvaluationModel toBusinessModel(@Valid @NotNull DnaViewModel model, boolean mutant) {
        return new EvaluationModelEntity(model.getDna(), mutant, Instant.now());
    }

    public StatsViewModel toViewModel(@NotNull StatsModel stats) {
        return new StatsViewModel(stats.getHumans(), stats.getMutants());
    }
}