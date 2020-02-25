package me.mrs.mutantes.converter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BaseDnaConverter implements AttributeConverter<List<String>, String> {

    public static final String DELIMITER = ".";

    @Override
    @NotNull
    public String convertToDatabaseColumn(@NotNull List<String> bases) {
        return String.join(DELIMITER, bases);
    }

    @Override
    @NotNull
    public List<String> convertToEntityAttribute(@NotNull String s) {
        return Arrays.asList(s.split(DELIMITER));
    }
}
