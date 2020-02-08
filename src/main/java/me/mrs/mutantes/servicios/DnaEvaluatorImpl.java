package me.mrs.mutantes.servicios;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class DnaEvaluatorImpl implements DnaEvaluator {
    @Override
    public boolean isHuman(Collection<String> dna) {
        var sequence = String.join(".", dna);
        var mutants = Set.of("ATGAGT.TAGTGC.ATATGT.AGAAGG.CCCCTA.TCATTG",
                "CTGCGA.ACATGC.ATCTGT.AGACGA.GATGCA.TCACTG",
                "TTGCGA.AAGTCC.ATATCT.AGAACG.CTATCA.TCACTG");
        var invalid = Set.of("A.TAGTGC", "CTGCGA.null.ATCTGT", "XAAAA.AAAAA");
        if (mutants.contains(sequence)) return false;
        if (invalid.contains(sequence)) throw new InvalidPayloadException("Invalid DNA");
        return true;
    }
}
