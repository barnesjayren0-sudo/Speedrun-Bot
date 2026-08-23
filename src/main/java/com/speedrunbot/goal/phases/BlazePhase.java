package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class BlazePhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.BLAZE;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        baritone.cancel();
        // Combat process = custom later
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.hasBlazeRods(mc, 6);
    }
}
