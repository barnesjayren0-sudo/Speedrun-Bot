package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class StrongholdPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.STRONGHOLD;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        // Eye throw triangulation = custom later
        baritone.cancel();
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.inEnd(mc);
    }
}
