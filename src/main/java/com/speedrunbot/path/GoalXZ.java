package com.speedrunbot.path;

public class GoalXZ implements Goal {

    public final int x, z;

    public GoalXZ(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean isInGoal(int px, int py, int pz) {
        return px == x && pz == z;
    }

    @Override
    public double heuristic(int px, int py, int pz) {
        double dx = px - x;
        double dz = pz - z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    @Override
    public String toString() {
        return "GoalXZ(" + x + "," + z + ")";
    }
}
