package me.mrs.mutantes;

import java.util.List;

public interface DnaEvaluator {
    boolean isMutant(String[] dna);

    boolean isMutant(List<String> dna);
}
