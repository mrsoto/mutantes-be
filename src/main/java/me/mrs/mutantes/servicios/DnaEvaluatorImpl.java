package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

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
        if (isRowMutant(dna)) return false;
        return !isColumnMutant(dna);
    }

    public boolean isColumnMutant(@NonNull Collection<String> dna) {
        if (dna.size() < 4) return false;

        int rowLength = dna.stream().findFirst().map(String::length).orElseThrow();
        return IntStream.range(0, rowLength).anyMatch(column -> {
            var collector = new HashMap<Character, Integer>();
            dna
                    .stream()
                    .map(seq -> seq.charAt(column))
                    .forEach(chr -> collector.merge(chr, 1, Integer::sum));
            return collector.values().stream().anyMatch(count -> count >= 4);
        });
    }

    public boolean isRowMutant(@NonNull Collection<String> dna) {
        var mutantSequence = List.of("AAAA", "CCCC", "TTTT", "GGGG");
        return dna.stream().anyMatch(seq -> mutantSequence.stream().anyMatch(seq::contains));
    }
}
