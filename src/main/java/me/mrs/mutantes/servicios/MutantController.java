package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.domain.DnaViewModel;
import me.mrs.mutantes.servicios.domain.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class MutantController {
    private final DnaEvaluator dnaEvaluator;
    private EvaluationsService evaluationsService;
    private ModelMapper modelMapper;

    public MutantController(
            @NonNull DnaEvaluator dnaEvaluator,
            @NonNull EvaluationsService evaluationsService,
            @NonNull ModelMapper modelMapper) {
        this.dnaEvaluator = dnaEvaluator;
        this.evaluationsService = evaluationsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/mutant/")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        boolean isMutant = dnaEvaluator.isMutant(payload.getDna());
        evaluationsService.registerEvaluation(modelMapper.toBusinessModel(payload, isMutant));
        return new ResponseEntity<>(isMutant ? HttpStatus.FORBIDDEN : HttpStatus.OK);
    }
}
