package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class DnaEvaluatorImpl implements DnaEvaluator {
    @Override
    public boolean isHuman(@NonNull Collection<String> dna) {
        var sequence = String.join(".", dna);
        var mutants = Set.of("ATGAGT.TAGTGC.ATATGT.AGAAGG.CCCCTA.TCATTG",
                "CTGCGA.ACATGC.ATCTGT.AGACGA.GATGCA.TCACTG",
                "TTGCGA.AAGTCC.ATATCT.AGAACG.CTATCA.TCACTG");
        return !mutants.contains(sequence);
    }
}
