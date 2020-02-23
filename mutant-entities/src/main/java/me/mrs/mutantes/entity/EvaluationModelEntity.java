package me.mrs.mutantes.entity;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.converter.DnaConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity(name = "evaluation")
@Table(name = "evaluations")
public class EvaluationModelEntity implements EvaluationModel {
    @Nullable
    private List<String> dna;
    @Nullable
    private Boolean mutant;
    @Nullable
    @Temporal(TemporalType.TIMESTAMP)
    private Instant timestamp;

    public EvaluationModelEntity() {
    }

    public EvaluationModelEntity(
            @NotNull List<String> dna, boolean mutant, @NotNull Instant timestamp) {
        this.dna = dna;
        this.mutant = mutant;
        this.timestamp = timestamp;
    }

    @Override
    @Convert(converter = DnaConverter.class)
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
