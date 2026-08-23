package com.speedrunbot.goal.phases;

import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

public class IronPhase implements PhaseHandler {
    @Override public RunPhase phase() { return RunPhase.IRON; }

    @Override
    public void enter(MinecraftClient mc, SRBaritone srb) {
        srb.mine(mc, "iron_ore");
    }

    @Override
    public boolean tick(MinecraftClient mc, SRBaritone srb) {
        return InventoryGoals.hasIronToolsBasics(mc);
    }
}
