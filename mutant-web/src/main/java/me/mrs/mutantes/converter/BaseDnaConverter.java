package me.mrs.mutantes.converter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BaseDnaConverter implements AttributeConverter<List<String>, String> {

    public static final String DELIMITER = ".";

    @Override
    @Nonnull
    public String convertToDatabaseColumn(@Nonnull List<String> bases) {
        return String.join(DELIMITER, bases);
    }

    @Override
    @Nonnull
    public List<String> convertToEntityAttribute(@Nonnull String s) {
        return Arrays.asList(s.split(DELIMITER));
    }
}
