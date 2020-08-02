package me.mrs.mutantes;

import javax.annotation.Nullable;

public interface StatsModel {
    long getMutants();

    long getHumans();

    @Nullable
    Double getRatio();
}
