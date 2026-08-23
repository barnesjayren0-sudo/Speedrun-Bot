package com.speedrunbot.path;

import net.minecraft.util.math.BlockPos;

public class GoalBlock implements Goal {

    public final int x, y, z;

    public GoalBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GoalBlock(BlockPos p) {
        this(p.getX(), p.getY(), p.getZ());
    }

    @Override
    public boolean isInGoal(int px, int py, int pz) {
        return px == x && py == y && pz == z;
    }

    @Override
    public double heuristic(int px, int py, int pz) {
        double dx = px - x;
        double dy = py - y;
        double dz = pz - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return "GoalBlock(" + x + "," + y + "," + z + ")";
    }
}
