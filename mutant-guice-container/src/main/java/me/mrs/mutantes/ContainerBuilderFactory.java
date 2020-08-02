package me.mrs.mutantes;

import me.mrs.mutantes.guice.GuiceContainerBuilder;

import javax.annotation.Nonnull;

public class ContainerBuilderFactory {
    private ContainerBuilderFactory() { }

    public static <T> ContainerBuilder<T> of(@Nonnull Class<T> klass) {
        return new GuiceContainerBuilder<>(klass);
    }
}
