package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.List;

public class EvaluationModel {
    private final List<String> dna;
    private final boolean mutant;
    private final Instant timestamp;

    public EvaluationModel(@NonNull List<String> dna, boolean mutant, @NonNull Instant timestamp) {
        this.dna = dna;
        this.mutant = mutant;
        this.timestamp = timestamp;
    }

    public List<String> getDna() {
        return dna;
    }

    public boolean isMutant() {
        return mutant;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
