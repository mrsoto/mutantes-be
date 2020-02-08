package me.mrs.mutantes.servicios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MutantController {
    @PostMapping(value = "/mutant")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@RequestBody final DnaViewModel payload) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
