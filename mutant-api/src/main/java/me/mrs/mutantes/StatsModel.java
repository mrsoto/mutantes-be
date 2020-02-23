package me.mrs.mutantes;

import org.jetbrains.annotations.Nullable;

public interface StatsModel {
    long getMutants();

    long getHumans();

    @Nullable Double getRatio();
}
