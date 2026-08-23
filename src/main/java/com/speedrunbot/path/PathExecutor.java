package com.speedrunbot.path;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PathExecutor {

    private List<BlockPos> path = new ArrayList<>();
    private int index;
    private boolean active;
    private long stuckSince;
    private Vec3d lastPos;
    private long lastMoveCheck;

    public void setPath(List<BlockPos> path) {
        this.path = path != null ? new ArrayList<>(path) : new ArrayList<>();
        this.index = this.path.size() > 1 ? 1 : 0;
        this.active = !this.path.isEmpty();
        this.stuckSince = 0;
        this.lastPos = null;
        this.lastMoveCheck = 0;
    }

    public void clear() {
        path.clear();
        index = 0;
        active = false;
        releaseMovement(MinecraftClient.getInstance());
    }

    public boolean isActive() {
        return active;
    }

    public int remaining() {
        return Math.max(0, path.size() - index);
    }

    public void tick(MinecraftClient mc) {
        if (!active || mc.player == null || mc.options == null) return;
        if (mc.currentScreen != null) {
            releaseMovement(mc);
            return;
        }
        if (index >= path.size()) {
            clear();
            return;
        }

        BlockPos target = path.get(index);
        Vec3d dest = new Vec3d(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        double dx = dest.x - mc.player.getX();
        double dz = dest.z - mc.player.getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);
        double dy = target.getY() - mc.player.getY();

        if (horiz < SRSettings.arriveDist && Math.abs(dy) < 1.3) {
            index++;
            stuckSince = 0;
            if (index >= path.size()) clear();
            return;
        }

        long now = System.currentTimeMillis();
        if (lastPos != null && now - lastMoveCheck > 350) {
            if (lastPos.squaredDistanceTo(mc.player.getPos()) < 0.03) {
                if (stuckSince == 0) stuckSince = now;
                else if (now - stuckSince > SRSettings.stuckMs) {
                    index = Math.min(index + 1, path.size());
                    stuckSince = 0;
                    press(mc.options.jumpKey, true);
                }
            } else {
                stuckSince = 0;
            }
            lastPos = mc.player.getPos();
            lastMoveCheck = now;
        } else if (lastPos == null) {
            lastPos = mc.player.getPos();
            lastMoveCheck = now;
        }

        lookToward(mc, dest.x, dest.y + 0.5, dest.z);

        press(mc.options.forwardKey, true);
        press(mc.options.backKey, false);
        press(mc.options.leftKey, false);
        press(mc.options.rightKey, false);
        press(mc.options.sprintKey, SRSettings.allowSprint && SRSettings.sprint && horiz > 1.8);

        boolean jump = (dy > 0.35 && mc.player.isOnGround())
                || (mc.player.horizontalCollision && mc.player.isOnGround());
        press(mc.options.jumpKey, jump);
        press(mc.options.sneakKey, dy < -1.8 && horiz < 1.0);
    }

    private void lookToward(MinecraftClient mc, double x, double y, double z) {
        Vec3d eyes = mc.player.getEyePos();
        double dx = x - eyes.x;
        double dy = y - eyes.y;
        double dz = z - eyes.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90f;
        float pitch = (float) (-(MathHelper.atan2(dy, horiz) * (180.0 / Math.PI)));
        pitch = MathHelper.clamp(pitch, -50f, 50f);

        float cy = mc.player.getYaw();
        float cp = mc.player.getPitch();
        float dyaw = MathHelper.wrapDegrees(yaw - cy);
        float s = SRSettings.lookSpeed;
        mc.player.setYaw(cy + dyaw * s);
        mc.player.setPitch(cp + (pitch - cp) * s);
    }

    private void press(KeyBinding key, boolean down) {
        try { key.setPressed(down); } catch (Exception ignored) {}
    }

    private void releaseMovement(MinecraftClient mc) {
        if (mc == null || mc.options == null) return;
        press(mc.options.forwardKey, false);
        press(mc.options.backKey, false);
        press(mc.options.leftKey, false);
        press(mc.options.rightKey, false);
        press(mc.options.jumpKey, false);
        press(mc.options.sprintKey, false);
        press(mc.options.sneakKey, false);
    }
}
