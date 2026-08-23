package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class FortressPhase implements PhaseHandler {

    private long enteredAt;

    @Override
    public RunPhase phase() {
        return RunPhase.FORTRESS;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        enteredAt = System.currentTimeMillis();
        // Explore outward — custom Baritone process later
        if (mc.player != null) {
            BlockPos p = mc.player.getBlockPos();
            baritone.pathToXZ(p.getX() + 64, p.getZ() + 64);
        }
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        // Placeholder: advance after explore window or when blaze rods start dropping
        // Real fortress detect needs structure scan / custom Baritone
        return System.currentTimeMillis() - enteredAt > 120_000;
    }
}
