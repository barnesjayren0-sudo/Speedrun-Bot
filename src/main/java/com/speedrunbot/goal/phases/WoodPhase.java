package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;

public class WoodPhase implements PhaseHandler {

    private boolean started;

    @Override
    public RunPhase phase() {
        return RunPhase.WOOD;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        started = true;
        // Prefer Baritone mine command when available
        if (!baritone.chatCommand("mine oak_log birch_log spruce_log 8")) {
            baritone.chatCommand("mine #log 8");
        }
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        if (!started) enter(mc, baritone);
        // enough logs or already has wooden pick
        return InventoryGoals.hasLogs(mc, 5) || InventoryGoals.hasPickaxeAtLeast(mc, "wood")
                || InventoryGoals.hasPickaxeAtLeast(mc, "stone");
    }
}
