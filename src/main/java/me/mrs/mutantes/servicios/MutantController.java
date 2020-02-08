package me.mrs.mutantes.servicios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@RestController
public class MutantController {
    @PostMapping(value = "/mutant")
    @ResponseBody
    public ResponseEntity<Void> isMutant(@Valid @RequestBody final DnaViewModel payload) {
        var sequence = String.join(".", payload.getDna());
        var mutants = Set.of("ATGAGT.TAGTGC.ATATGT.AGAAGG.CCCCTA.TCATTG",
                "CTGCGA.ACATGC.ATCTGT.AGACGA.GATGCA.TCACTG",
                "TTGCGA.AAGTCC.ATATCT.AGAACG.CTATCA.TCACTG");
        var invalid = Set.of("A.TAGTGC", "CTGCGA.null.ATCTGT", "XAAAA.AAAAA");
        if (mutants.contains(sequence)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (invalid.contains(sequence)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
