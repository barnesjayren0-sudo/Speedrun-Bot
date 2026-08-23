package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class PearlsPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.PEARLS;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        baritone.cancel();
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.hasPearls(mc, 12) || InventoryGoals.hasEyes(mc, 12);
    }
}
