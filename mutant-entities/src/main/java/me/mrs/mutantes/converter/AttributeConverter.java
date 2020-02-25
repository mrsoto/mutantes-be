package me.mrs.mutantes.converter;

import org.jetbrains.annotations.NotNull;

public interface AttributeConverter<T, U> {
    @NotNull U convertToDatabaseColumn(@NotNull T bases);

    @NotNull T convertToEntityAttribute(@NotNull U s);
}
