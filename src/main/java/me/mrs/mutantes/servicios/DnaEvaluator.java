package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;

import java.util.Collection;

public interface DnaEvaluator {
    boolean isHuman(@NonNull Collection<String> dna);
}
