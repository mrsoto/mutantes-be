package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

public class QueryModel {
    private final String dna;
    private final boolean isMutant;
    private Instant instant;

    public QueryModel(String dna, boolean isMutant) {
        this.dna = dna;
        this.isMutant = isMutant;
        this.instant = Instant.now();
    }

    public QueryModel(@NonNull @NotEmpty String dna, boolean isMutant, Instant instant) {
        this(dna, isMutant);
        this.instant = instant;
    }

    public @NonNull
    String getDna() {
        return dna;
    }

    public boolean getMutant() {
        return isMutant;
    }

    public Instant getInstant() {
        return instant;
    }
}
