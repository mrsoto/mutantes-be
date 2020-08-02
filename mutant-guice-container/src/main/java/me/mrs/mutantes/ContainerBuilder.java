package me.mrs.mutantes;

import io.logz.guice.jersey.configuration.JerseyConfiguration;

@FunctionalInterface
public interface ContainerBuilder<T> {
    T create(JerseyConfiguration configuration);
}
