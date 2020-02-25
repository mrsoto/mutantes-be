package me.mrs.mutantes.entity;

import me.mrs.mutantes.EvaluationModel;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class EvaluationModelEntity implements EvaluationModel {
    private List<String> dna;
    private Boolean mutant;
    private Instant timestamp;

    public EvaluationModelEntity(
            @NotNull List<String> dna, boolean mutant, @NotNull Instant timestamp) {
        this.dna = dna;
        this.mutant = mutant;
        this.timestamp = timestamp;
    }

    @Override
    @NotNull
    public List<String> getDna() {
        return Objects.requireNonNull(dna);
    }

    @Override
    public boolean isMutant() {
        return Objects.requireNonNull(mutant);
    }

    @Override
    @NotNull
    public Instant getTimestamp() {
        return Objects.requireNonNull(timestamp);
    }
}
