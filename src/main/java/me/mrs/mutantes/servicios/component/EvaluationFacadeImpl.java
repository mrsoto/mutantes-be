package me.mrs.mutantes.servicios.component;

import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvaluationFacadeImpl implements EvaluationFacade {

    private final DnaEvaluator dnaEvaluator;
    private final EvaluationsService evaluationsService;
    private final ModelMapper modelMapper;

    public EvaluationFacadeImpl(
            @NonNull DnaEvaluator dnaEvaluator,
            @NonNull EvaluationsService evaluationsService,
            @NonNull ModelMapper modelMapper) {
        this.dnaEvaluator = dnaEvaluator;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean isMutant(@NonNull List<String> dna) {
        var mutant = dnaEvaluator.isMutant(dna.toArray(new String[0]));
        var model = modelMapper.toBusinessModel(dna, mutant);
        evaluationsService.registerEvaluation(model);
        return mutant;
    }
}
