package com.speedrunbot.goal.phases;

import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

public class FortressPhase implements PhaseHandler {
    private long enteredAt;

    @Override public RunPhase phase() { return RunPhase.FORTRESS; }

    @Override
    public void enter(MinecraftClient mc, SRBaritone srb) {
        enteredAt = System.currentTimeMillis();
        if (mc.player != null) {
            srb.gotoXZ(mc, mc.player.getBlockX() + 48, mc.player.getBlockZ() + 48);
        }
    }

    @Override
    public boolean tick(MinecraftClient mc, SRBaritone srb) {
        return System.currentTimeMillis() - enteredAt > 120_000;
    }
}
