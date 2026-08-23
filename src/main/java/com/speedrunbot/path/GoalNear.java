package com.speedrunbot.path;

public class GoalNear implements Goal {

    public final int x, y, z;
    public final int range;

    public GoalNear(int x, int y, int z, int range) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = Math.max(1, range);
    }

    @Override
    public boolean isInGoal(int px, int py, int pz) {
        int dx = Math.abs(px - x);
        int dy = Math.abs(py - y);
        int dz = Math.abs(pz - z);
        return dx + dy + dz <= range;
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
        return "GoalNear(" + x + "," + y + "," + z + ",r=" + range + ")";
    }
}
