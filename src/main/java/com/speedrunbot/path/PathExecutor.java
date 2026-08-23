package com.speedrunbot.path;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/** Walks a list of BlockPos by aiming + holding W / jump / sprint. */
public class PathExecutor {

    private List<BlockPos> path = new ArrayList<>();
    private int index;
    private boolean active;
    private long stuckSince;
    private double lastDist = Double.MAX_VALUE;

    public void setPath(List<BlockPos> path) {
        this.path = path != null ? new ArrayList<>(path) : new ArrayList<>();
        this.index = 0;
        this.active = !this.path.isEmpty();
        this.stuckSince = 0;
        this.lastDist = Double.MAX_VALUE;
        if (this.path.size() > 1) this.index = 1; // skip current block
    }

    public void clear() {
        path.clear();
        index = 0;
        active = false;
        releaseKeys(MinecraftClient.getInstance());
    }

    public boolean isActive() {
        return active;
    }

    public int remaining() {
        return Math.max(0, path.size() - index);
    }

    public void tick(MinecraftClient mc) {
        if (!active || mc.player == null || mc.options == null) return;
        if (index >= path.size()) {
            clear();
            return;
        }

        BlockPos target = path.get(index);
        Vec3d eyes = mc.player.getEyePos();
        Vec3d dest = new Vec3d(target.getX() + 0.5, target.getY() + 0.1, target.getZ() + 0.5);
        double dist = eyes.distanceTo(dest);

        // reached node
        if (dist < 0.85 || (Math.abs(mc.player.getX() - dest.x) < 0.6
                && Math.abs(mc.player.getZ() - dest.z) < 0.6
                && Math.abs(mc.player.getY() - target.getY()) < 1.2)) {
            index++;
            stuckSince = 0;
            lastDist = Double.MAX_VALUE;
            if (index >= path.size()) {
                clear();
            }
            return;
        }

        // stuck detection
        if (dist >= lastDist - 0.01) {
            if (stuckSince == 0) stuckSince = System.currentTimeMillis();
            else if (System.currentTimeMillis() - stuckSince > 2500) {
                // skip node or jump spam
                index++;
                stuckSince = 0;
                press(mc.options.jumpKey, true);
                return;
            }
        } else {
            stuckSince = 0;
        }
        lastDist = dist;

        lookAt(mc, dest);

        press(mc.options.forwardKey, true);
        press(mc.options.sprintKey, true);

        // jump if need up
        boolean needJump = target.getY() > mc.player.getBlockY()
                || mc.player.horizontalCollision;
        press(mc.options.jumpKey, needJump && mc.player.isOnGround());

        press(mc.options.sneakKey, false);
        press(mc.options.backKey, false);
        press(mc.options.leftKey, false);
        press(mc.options.rightKey, false);
    }

    private void lookAt(MinecraftClient mc, Vec3d dest) {
        Vec3d eyes = mc.player.getEyePos();
        double dx = dest.x - eyes.x;
        double dy = dest.y - eyes.y;
        double dz = dest.z - eyes.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90f;
        float pitch = (float) (-(MathHelper.atan2(dy, horiz) * (180.0 / Math.PI)));
        pitch = MathHelper.clamp(pitch, -60f, 60f);

        // smooth turn
        float cy = mc.player.getYaw();
        float cp = mc.player.getPitch();
        float dyaw = MathHelper.wrapDegrees(yaw - cy);
        mc.player.setYaw(cy + dyaw * 0.45f);
        mc.player.setPitch(cp + (pitch - cp) * 0.35f);
    }

    private void press(KeyBinding key, boolean down) {
        try {
            key.setPressed(down);
        } catch (Exception ignored) {}
    }

    private void releaseKeys(MinecraftClient mc) {
        if (mc == null || mc.options == null) return;
        press(mc.options.forwardKey, false);
        press(mc.options.backKey, false);
        press(mc.options.leftKey, false);
        press(mc.options.rightKey, false);
        press(mc.options.jumpKey, false);
        press(mc.options.sprintKey, false);
    }
}
