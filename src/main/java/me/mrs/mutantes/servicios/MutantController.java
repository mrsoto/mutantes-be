package me.mrs.mutantes.servicios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RestController
public class MutantController {
    private final DnaEvaluator evaluatorService;

    public MutantController(@NonNull DnaEvaluator evaluatorService) {
        this.evaluatorService = evaluatorService;
    }

    @PostMapping(value = "/mutant")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        boolean isMutan = evaluatorService.isMutant(payload.getDna());
        return new ResponseEntity<>(isMutan ? HttpStatus.FORBIDDEN : HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public RedirectView redirectWithUsingRedirectView() {
        return new RedirectView("/docs/index.html");
    }
}
