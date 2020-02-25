package me.mrs.mutantes.entity;

import me.mrs.mutantes.EvaluationModel;
import me.mrs.mutantes.converter.DnaConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Entity(name = "evaluation")
@Table(name = "evaluations")
public class EvaluationModelEntity implements EvaluationModel {
    private static final AtomicInteger keyGenerator = new AtomicInteger(0);

    @Id
    long id;

    @Nullable
    @Convert(converter = DnaConverter.class)
    private List<String> dna;
    @Nullable
    private Boolean mutant;
    @Nullable
    @Column(name = "instant")
    private Instant timestamp;

    public EvaluationModelEntity() {
        id = keyGenerator.incrementAndGet();
    }

    public EvaluationModelEntity(
            @NotNull List<String> dna, boolean mutant, @NotNull Instant timestamp) {
        this();
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
