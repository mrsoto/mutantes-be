package me.mrs.mutantes.persistence;

import javax.annotation.Nonnull;
import java.sql.ResultSet;

@FunctionalInterface
public interface ResultRowMapper<T> {
    @SuppressWarnings("squid:S00112")
    @Nonnull
    T mapRow(@Nonnull ResultSet resultSet) throws Exception;
}
