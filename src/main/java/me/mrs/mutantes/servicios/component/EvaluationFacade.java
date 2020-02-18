package me.mrs.mutantes.servicios.component;

import org.springframework.lang.NonNull;

import java.util.List;

public interface EvaluationFacade {
    boolean isMutant(@NonNull List<String> dna);
}
