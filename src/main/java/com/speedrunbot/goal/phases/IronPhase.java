package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class IronPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.IRON;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        baritone.chatCommand("mine iron_ore 12");
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.hasIronToolsBasics(mc);
    }
}
