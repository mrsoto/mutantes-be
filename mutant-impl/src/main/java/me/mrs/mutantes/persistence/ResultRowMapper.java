package me.mrs.mutantes.persistence;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultRowMapper<T> {
    @SuppressWarnings("squid:S00112")
    @NotNull T mapRow(@NotNull ResultSet resultSet) throws Exception;
}
