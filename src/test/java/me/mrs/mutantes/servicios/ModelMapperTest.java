package me.mrs.mutantes.servicios;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class ModelMapperTest {

    @Test
    void toBusinessModel() {
        var sequence = List.of("AAAA");
        DnaViewModel source = new DnaViewModel(sequence);
        var target = new ModelMapper();
        var start = Instant.now();
        var dest = target.toBusinessModel(source, true);
        Assertions.assertNotNull(dest);
        Assertions.assertAll("Comparing field by field",
                () -> Assertions.assertEquals(source.getDna(), dest.getDna()),
                () -> Assertions.assertTrue(dest.isMutant()),
                () -> Assertions.assertEquals(start, dest.getTimestamp()));
    }
}