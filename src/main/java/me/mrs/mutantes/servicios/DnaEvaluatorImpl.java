package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class DnaEvaluatorImpl implements DnaEvaluator {

    /**
     * Evaluate a DNA looking for mutations. A mutation is identified by for linearly continuous
     * symbols in any direction.
     * <p>
     * Preconditions: DNA should be valid, See {@link DnaValidator}
     *
     * @param dna DNA to be tested
     * @return {@code true} when no mutation exists, {@code false} when a mutation is identified
     */
    @Override
    public boolean isHuman(@NonNull Collection<String> dna) {
        var mutantSequence = List.of("AAAA", "CCCC", "TTTT", "GGGG");
        return dna.stream().noneMatch(seq -> mutantSequence.stream().anyMatch(seq::contains));
    }
}
