package me.mrs.mutantes.servicios.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelMapperTest {

    @Test
    void toBusinessModel() {
        var sequence = List.of("AAAA");
        DnaViewModel source = new DnaViewModel(sequence);
        var target = new ModelMapper();
        var start = Instant.now();
        var dest = target.toBusinessModel(source.getDna(), true);
        assertNotNull(dest);
        assertAll("Comparing field by field",
                () -> assertEquals(source.getDna(), dest.getDna()),
                () -> assertTrue(dest.isMutant()),
                () -> assertTrue(start.toEpochMilli() - dest.getTimestamp().toEpochMilli() < 4));
    }

    @Test
    void toViewModel() {
        var target = new ModelMapper();
        var stats = new StatsModel(10L, 4L);
        var actual = target.toViewModel(stats);

        assertNotNull(actual);
        assertEquals(10L, actual.getHumans());
        assertEquals(4L, actual.getMutants());
        assertEquals(0.4d, actual.getRatio());
    }
}