package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class NetherPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.NETHER;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        // Portal building is custom logic — for now just detect dimension
        baritone.cancel();
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.inNether(mc);
    }
}
