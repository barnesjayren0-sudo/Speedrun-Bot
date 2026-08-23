package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class StonePhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.STONE;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        baritone.chatCommand("mine stone 20");
        baritone.chatCommand("mine coal_ore 5");
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        return InventoryGoals.hasPickaxeAtLeast(mc, "stone")
                || InventoryGoals.hasPickaxeAtLeast(mc, "iron");
    }
}
