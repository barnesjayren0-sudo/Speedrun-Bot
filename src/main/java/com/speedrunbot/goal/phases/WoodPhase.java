package com.speedrunbot.goal.phases;

import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

public class WoodPhase implements PhaseHandler {
    @Override public RunPhase phase() { return RunPhase.WOOD; }

    @Override
    public void enter(MinecraftClient mc, SRBaritone srb) {
        srb.mine(mc, "oak_log");
    }

    @Override
    public boolean tick(MinecraftClient mc, SRBaritone srb) {
        return InventoryGoals.hasLogs(mc, 5)
                || InventoryGoals.hasPickaxeAtLeast(mc, "wood")
                || InventoryGoals.hasPickaxeAtLeast(mc, "stone");
    }
}
