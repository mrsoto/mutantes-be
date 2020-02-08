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
        return new ResponseEntity<>(evaluatorService.isHuman(payload.getDna()) ? HttpStatus.OK :
                HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/")
    public RedirectView redirectWithUsingRedirectView() {
        return new RedirectView("/docs/index.html");
    }
}
