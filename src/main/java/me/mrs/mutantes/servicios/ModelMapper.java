package me.mrs.mutantes.servicios;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ModelMapper {
    public EvaluationModel toBusinessModel(DnaViewModel model, boolean mutant) {
        return new EvaluationModel(model.getDna(), mutant, Instant.now());
    }
}
