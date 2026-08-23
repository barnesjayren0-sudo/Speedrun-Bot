package com.speedrunbot.goal.phases;

import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

public class BlazePhase implements PhaseHandler {
    @Override public RunPhase phase() { return RunPhase.BLAZE; }

    @Override
    public void enter(MinecraftClient mc, SRBaritone srb) {
        srb.stop(mc);
    }

    @Override
    public boolean tick(MinecraftClient mc, SRBaritone srb) {
        return InventoryGoals.hasBlazeRods(mc, 6);
    }
}
