package com.speedrunbot.task.chains;

import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/** If player position barely changes while pathing — jump / repath (AltoClef Unstuck). */
public class UnstuckChain {

    private Vec3d last;
    private long stillSince;
    private long lastFix;

    public void tick(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null || srb == null) return;
        if (!srb.isPathing() && !srb.isRunning()) {
            last = null;
            return;
        }
        Vec3d pos = mc.player.getPos();
        long now = System.currentTimeMillis();
        if (last == null) {
            last = pos;
            stillSince = now;
            return;
        }
        if (last.squaredDistanceTo(pos) < 0.05) {
            if (now - stillSince > 2200 && now - lastFix > 3000) {
                mc.options.jumpKey.setPressed(true);
                // nudge look
                mc.player.setYaw(mc.player.getYaw() + 35);
                lastFix = now;
                stillSince = now;
            }
        } else {
            stillSince = now;
            last = pos;
        }
    }
}
