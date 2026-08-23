package com.speedrunbot.path;

public final class PathNode {
    public final int x, y, z;
    public double g, f;
    public PathNode parent;

    public PathNode(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long key() {
        return BlockKeys.pack(x, y, z);
    }
}
