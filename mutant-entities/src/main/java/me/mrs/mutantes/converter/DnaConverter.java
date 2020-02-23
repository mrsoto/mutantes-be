package me.mrs.mutantes.converter;

import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.List;

public class DnaConverter implements AttributeConverter<List<String>, String> {
    @Override
    @NotNull
    public String convertToDatabaseColumn(@NotNull List<String> bases) {
        return String.join(".", bases);
    }

    @Override
    @NotNull
    public List<String> convertToEntityAttribute(@NotNull String s) {
        return Arrays.asList(s.split("."));
    }
}
