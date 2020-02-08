package me.mrs.mutantes.servicios;

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
    private final DnaEvaluator evaluatorService;

    public MutantController(@NonNull DnaEvaluator evaluatorService) {
        this.evaluatorService = evaluatorService;
    }

    @PostMapping(value = "/mutant")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        return new ResponseEntity<>(evaluatorService.isHuman(payload.getDna()) ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }
}
