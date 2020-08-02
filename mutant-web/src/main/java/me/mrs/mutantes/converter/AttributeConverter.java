package me.mrs.mutantes.converter;
import javax.annotation.Nonnull;

public interface AttributeConverter<T, U> {
    @Nonnull
    U convertToDatabaseColumn(@Nonnull T bases);

    @Nonnull
    T convertToEntityAttribute(@Nonnull U s);
}
