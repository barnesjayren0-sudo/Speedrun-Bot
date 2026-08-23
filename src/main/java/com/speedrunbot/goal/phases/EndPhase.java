package com.speedrunbot.goal.phases;

import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class EndPhase implements PhaseHandler {
    @Override public RunPhase phase() { return RunPhase.END; }

    @Override
    public void enter(MinecraftClient mc, SRBaritone srb) {
        srb.stop(mc);
    }

    @Override
    public boolean tick(MinecraftClient mc, SRBaritone srb) {
        if (!InventoryGoals.inEnd(mc) || mc.world == null) return false;
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof EnderDragonEntity d && d.isAlive()) return false;
        }
        return true;
    }
}
