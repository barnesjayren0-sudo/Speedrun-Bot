package com.speedrunbot.task.chains;

import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Hand;

/** Hit nearby hostiles; interrupt user task briefly (AltoClef MobDefense lite). */
public class MobDefenseChain {

    private long lastSwing;

    /** @return true if this tick should skip user task */
    public boolean tickInterrupt(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return false;

        HostileEntity closest = null;
        double best = 6.0;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof HostileEntity h) || !h.isAlive()) continue;
            double d = mc.player.distanceTo(h);
            if (d < best) {
                best = d;
                closest = h;
            }
        }
        if (closest == null) return false;

        // look + hit
        var eyes = mc.player.getEyePos();
        var t = closest.getEyePos();
        double dx = t.x - eyes.x;
        double dy = t.y - eyes.y;
        double dz = t.z - eyes.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        mc.player.setYaw((float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90f);
        mc.player.setPitch((float) (-(Math.atan2(dy, horiz) * (180 / Math.PI))));

        if (best < 3.2 && System.currentTimeMillis() - lastSwing > 550) {
            mc.interactionManager.attackEntity(mc.player, closest);
            mc.player.swingHand(Hand.MAIN_HAND);
            lastSwing = System.currentTimeMillis();
        }
        return best < 4.0;
    }
}
