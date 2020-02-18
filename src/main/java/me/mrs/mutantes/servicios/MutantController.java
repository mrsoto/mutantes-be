package me.mrs.mutantes.servicios;

import me.mrs.mutantes.servicios.component.EvaluationFacade;
import me.mrs.mutantes.servicios.domain.DnaViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class MutantController {
    private final EvaluationFacade evaluationFacade;

    public MutantController(
            @NonNull EvaluationFacade evaluationFacade) {
        this.evaluationFacade = evaluationFacade;
    }

    private static HttpStatus toHttpStatusCode(Boolean isMutant) {
        return Boolean.TRUE.equals(isMutant) ? HttpStatus.FORBIDDEN : HttpStatus.OK;
    }

    @PostMapping(value = "/mutant/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<Void>> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        return Mono
                .just(payload.getDna())
                .map(evaluationFacade::isMutant)
                .map(MutantController::toHttpStatusCode)
                .map(ResponseEntity::new);
    }
}
