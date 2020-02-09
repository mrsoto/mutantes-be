package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;

import java.util.List;

public interface DnaEvaluator {
    boolean isMutant(@NonNull String[] dna);

    boolean isMutant(@NonNull List<String> dna);
}
