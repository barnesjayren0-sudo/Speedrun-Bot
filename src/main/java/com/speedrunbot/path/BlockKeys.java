package com.speedrunbot.path;

public final class BlockKeys {
    private BlockKeys() {}

    public static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (long) (z & 0x3FFFFFF);
    }
}
