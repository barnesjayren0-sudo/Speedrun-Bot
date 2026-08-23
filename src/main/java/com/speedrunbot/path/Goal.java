package com.speedrunbot.path;

import net.minecraft.util.math.BlockPos;

public interface Goal {
    boolean isInGoal(int x, int y, int z);

    double heuristic(int x, int y, int z);

    default boolean isInGoal(BlockPos p) {
        return isInGoal(p.getX(), p.getY(), p.getZ());
    }
}
