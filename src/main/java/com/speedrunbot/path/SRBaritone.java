package com.speedrunbot.path;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Our own mini-Baritone: goals, A*, executor, # commands including #start.
 */
public class SRBaritone {

    private final PathExecutor executor = new PathExecutor();
    private Goal currentGoal;
    private boolean running;
    private boolean paused;
    private String status = "idle";
    private Block mineTarget;
    private long lastRepath;

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        if (mineTarget != null && running && !paused) {
            tickMine(mc);
            return;
        }

        if (running && !paused) {
            if (!executor.isActive() && currentGoal != null) {
                repath(mc);
            }
            executor.tick(mc);
            if (!executor.isActive() && currentGoal != null
                    && currentGoal.isInGoal(mc.player.getBlockPos())) {
                status = "arrived";
                running = false;
                msg(mc, "§aArrived");
            }
        }
    }

    private void tickMine(MinecraftClient mc) {
        // Find nearest matching block in loaded chunks around player
        BlockPos player = mc.player.getBlockPos();
        BlockPos found = findNearest(mc, player, mineTarget, 48);
        if (found == null) {
            status = "mine: searching";
            if (System.currentTimeMillis() - lastRepath > 3000) {
                // wander a bit
                lastRepath = System.currentTimeMillis();
            }
            return;
        }
        if (player.getManhattanDistance(found) <= 4) {
            // look and dig
            status = "mine: digging";
            lookAtBlock(mc, found);
            mc.options.attackKey.setPressed(true);
            if (mc.world.getBlockState(found).isAir()) {
                mc.options.attackKey.setPressed(false);
                executor.clear();
            }
            return;
        }
        mc.options.attackKey.setPressed(false);
        if (!executor.isActive() || System.currentTimeMillis() - lastRepath > 4000) {
            currentGoal = new GoalBlock(found);
            List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, 20000);
            executor.setPath(path);
            lastRepath = System.currentTimeMillis();
            status = "mine: pathing " + path.size();
        }
        executor.tick(mc);
    }

    private BlockPos findNearest(MinecraftClient mc, BlockPos origin, Block block, int range) {
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos best = null;
        int bestD = Integer.MAX_VALUE;
        int r = Math.min(range, 40);
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -16; dy <= 16; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    m.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!mc.world.isChunkLoaded(ChunkPos.toLong(m.getX() >> 4, m.getZ() >> 4))
                            && !mc.world.isChunkLoaded(m)) {
                        // try getBlockState anyway on client
                    }
                    BlockState st = mc.world.getBlockState(m);
                    if (st.isOf(block)) {
                        int d = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                        if (d < bestD) {
                            bestD = d;
                            best = m.toImmutable();
                        }
                    }
                }
            }
        }
        return best;
    }

    private void lookAtBlock(MinecraftClient mc, BlockPos p) {
        var eyes = mc.player.getEyePos();
        var dest = new net.minecraft.util.math.Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
        double dx = dest.x - eyes.x;
        double dy = dest.y - eyes.y;
        double dz = dest.z - eyes.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90f;
        float pitch = (float) (-(Math.atan2(dy, horiz) * (180 / Math.PI)));
        mc.player.setYaw(yaw);
        mc.player.setPitch(Math.max(-90, Math.min(90, pitch)));
    }

    public void setGoalAndPath(MinecraftClient mc, Goal goal) {
        this.currentGoal = goal;
        this.mineTarget = null;
        this.paused = false;
        this.running = true;
        repath(mc);
        status = "pathing";
        msg(mc, "§aGoal §f" + goal + " §7nodes=" + executor.remaining());
    }

    private void repath(MinecraftClient mc) {
        if (currentGoal == null) return;
        List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, 25000);
        executor.setPath(path);
        lastRepath = System.currentTimeMillis();
        if (path.isEmpty()) {
            status = "no path";
            msg(mc, "§cNo path found");
        }
    }

    /** #start — begin / resume following current goal */
    public void start(MinecraftClient mc) {
        paused = false;
        running = true;
        if (currentGoal != null && !executor.isActive()) repath(mc);
        status = "started";
        msg(mc, "§a#start");
    }

    public void stop(MinecraftClient mc) {
        running = false;
        paused = false;
        mineTarget = null;
        executor.clear();
        try {
            mc.options.attackKey.setPressed(false);
        } catch (Exception ignored) {}
        status = "stopped";
        msg(mc, "§c#stop");
    }

    public void pause(MinecraftClient mc) {
        paused = true;
        executor.clear();
        status = "paused";
        msg(mc, "§e#pause");
    }

    public void resume(MinecraftClient mc) {
        start(mc);
        msg(mc, "§a#resume");
    }

    public void gotoPos(MinecraftClient mc, int x, int y, int z) {
        setGoalAndPath(mc, new GoalBlock(x, y, z));
    }

    public void gotoXZ(MinecraftClient mc, int x, int z) {
        setGoalAndPath(mc, new GoalXZ(x, z));
    }

    public void mine(MinecraftClient mc, String blockId) {
        Identifier id = Identifier.tryParse(blockId.contains(":") ? blockId : "minecraft:" + blockId);
        if (id == null || !Registries.BLOCK.containsId(id)) {
            msg(mc, "§cUnknown block: " + blockId);
            return;
        }
        mineTarget = Registries.BLOCK.get(id);
        running = true;
        paused = false;
        status = "mine " + blockId;
        msg(mc, "§a#mine " + blockId);
    }

    public boolean isRunning() {
        return running && !paused;
    }

    public boolean isPathing() {
        return executor.isActive();
    }

    public String getStatus() {
        return status;
    }

    public Goal getGoal() {
        return currentGoal;
    }

    private void msg(MinecraftClient mc, String s) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§8[§bSRB§8] " + s), false);
        }
    }
}
