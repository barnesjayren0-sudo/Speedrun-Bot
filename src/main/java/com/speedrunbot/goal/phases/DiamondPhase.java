package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class DiamondPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.DIAMOND;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        // Classic strip mine depth — user/custom Baritone can refine
        baritone.chatCommand("mine diamond_ore 8");
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.hasDiamonds(mc, 3);
    }
}
