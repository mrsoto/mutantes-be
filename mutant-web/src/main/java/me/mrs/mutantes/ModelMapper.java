package me.mrs.mutantes;

import me.mrs.mutantes.entity.EvaluationModelEntity;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.validation.Valid;
import java.time.Instant;

@Singleton
public class ModelMapper {
    public EvaluationModel toBusinessModel(@Valid @Nonnull DnaViewModel model, boolean mutant) {
        return new EvaluationModelEntity(model.getDna(), mutant, Instant.now());
    }

    public StatsViewModel toViewModel(@Nonnull StatsModel stats) {
        return new StatsViewModel(stats.getHumans(), stats.getMutants());
    }
}
