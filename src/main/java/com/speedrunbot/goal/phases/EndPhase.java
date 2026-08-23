package com.speedrunbot.goal.phases;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.goal.InventoryGoals;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class EndPhase implements PhaseHandler {

    @Override
    public RunPhase phase() {
        return RunPhase.END;
    }

    @Override
    public void enter(MinecraftClient mc, BaritoneBridge baritone) {
        baritone.cancel();
    }

    @Override
    public boolean tick(MinecraftClient mc, BaritoneBridge baritone) {
        if (!InventoryGoals.inEnd(mc) || mc.world == null) return false;
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof EnderDragonEntity dragon && dragon.isAlive()) {
                return false;
            }
        }
        // no living dragon found
        return true;
    }
}
