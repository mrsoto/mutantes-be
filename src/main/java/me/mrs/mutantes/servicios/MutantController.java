package me.mrs.mutantes.servicios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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

    @PostMapping(value = "/mutant")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        boolean isMutan = dnaEvaluator.isMutant(payload.getDna());
        evaluationsService.registerEvaluation(modelMapper.toBusinessModel(payload, isMutan));
        return new ResponseEntity<>(isMutan ? HttpStatus.FORBIDDEN : HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public RedirectView redirectWithUsingRedirectView() {
        return new RedirectView("/docs/index.html");
    }
}
