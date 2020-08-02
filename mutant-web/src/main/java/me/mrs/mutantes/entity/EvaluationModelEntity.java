package me.mrs.mutantes.entity;

import me.mrs.mutantes.EvaluationModel;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class EvaluationModelEntity implements EvaluationModel {
    private List<String> dna;
    private Boolean mutant;
    private Instant timestamp;

    public EvaluationModelEntity(
            @Nonnull List<String> dna, boolean mutant, @Nonnull Instant timestamp
    ) {
        this.dna = dna;
        this.mutant = mutant;
        this.timestamp = timestamp;
    }

    @Override
    @Nonnull
    public List<String> getDna() {
        return Objects.requireNonNull(dna);
    }

    @Override
    public boolean isMutant() {
        return Objects.requireNonNull(mutant);
    }

    @Override
    @Nonnull
    public Instant getTimestamp() {
        return Objects.requireNonNull(timestamp);
    }
}
