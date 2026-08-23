package com.speedrunbot.path;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SRBaritone {

    private final PathExecutor executor = new PathExecutor();
    private Goal currentGoal;
    private boolean running;
    private boolean paused;
    private String status = "idle";
    private final List<Block> mineBlocks = new ArrayList<>();
    private long lastRepath;
    private long lastChat;
    private BlockPos mineFocus;
    private int emptyPathStreak;

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        if (!running || paused) return;

        if (!mineBlocks.isEmpty()) {
            tickMine(mc);
            return;
        }

        if (currentGoal != null && currentGoal.isInGoal(
                mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ())) {
            status = "arrived";
            running = false;
            executor.clear();
            msg(mc, "§aArrived");
            return;
        }

        if (!executor.isActive() && currentGoal != null) {
            if (System.currentTimeMillis() - lastRepath > SRSettings.repathMs) {
                repath(mc);
            }
        }
        executor.tick(mc);
    }

    private void tickMine(MinecraftClient mc) {
        BlockPos player = mc.player.getBlockPos();

        if (mineFocus == null || mc.world.getBlockState(mineFocus).isAir()
                || !matchesMine(mc.world.getBlockState(mineFocus))) {
            mineFocus = findNearest(mc, player, SRSettings.mineSearchRange);
            lastRepath = 0;
        }

        if (mineFocus == null) {
            status = "mine: search";
            executor.clear();
            // wander slightly forward to load new chunks
            if (System.currentTimeMillis() - lastRepath > 5000) {
                float yaw = mc.player.getYaw();
                double rad = Math.toRadians(yaw);
                int tx = player.getX() + (int) Math.round(-Math.sin(rad) * 24);
                int tz = player.getZ() + (int) Math.round(Math.cos(rad) * 24);
                currentGoal = new GoalXZ(tx, tz);
                repath(mc);
                lastRepath = System.currentTimeMillis();
            }
            return;
        }

        double dist = mc.player.getEyePos().distanceTo(Vec3d.ofCenter(mineFocus));
        if (dist <= 4.2) {
            status = "mine: dig";
            executor.clear();
            lookAt(mc, mineFocus);
            try {
                mc.options.attackKey.setPressed(true);
            } catch (Exception ignored) {}
            if (mc.world.getBlockState(mineFocus).isAir()) {
                try { mc.options.attackKey.setPressed(false); } catch (Exception ignored) {}
                mineFocus = null;
            }
            return;
        }

        try { mc.options.attackKey.setPressed(false); } catch (Exception ignored) {}

        if (!executor.isActive() || System.currentTimeMillis() - lastRepath > SRSettings.repathMs) {
            // stand near block
            currentGoal = new GoalNear(mineFocus.getX(), mineFocus.getY(), mineFocus.getZ(), 2);
            List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, SRSettings.maxPathNodes);
            executor.setPath(path);
            lastRepath = System.currentTimeMillis();
            if (path.isEmpty()) {
                emptyPathStreak++;
                status = "mine: no path";
                if (emptyPathStreak > 3) {
                    mineFocus = null;
                    emptyPathStreak = 0;
                }
            } else {
                emptyPathStreak = 0;
                status = "mine: path " + path.size();
            }
        }
        executor.tick(mc);
    }

    private boolean matchesMine(BlockState st) {
        for (Block b : mineBlocks) {
            if (st.isOf(b)) return true;
        }
        return false;
    }

    private BlockPos findNearest(MinecraftClient mc, BlockPos origin, int range) {
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos best = null;
        int bestD = Integer.MAX_VALUE;
        int r = Math.min(range, 48);
        int bottom = mc.world.getBottomY();
        int top = bottom + mc.world.getHeight();

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -24; dy <= 24; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    int y = origin.getY() + dy;
                    if (y < bottom || y >= top) continue;
                    m.set(origin.getX() + dx, y, origin.getZ() + dz);
                    if (!matchesMine(mc.world.getBlockState(m))) continue;
                    int d = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (d < bestD) {
                        bestD = d;
                        best = m.toImmutable();
                    }
                }
            }
        }
        return best;
    }

    private void lookAt(MinecraftClient mc, BlockPos p) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d dest = Vec3d.ofCenter(p);
        double dx = dest.x - eyes.x;
        double dy = dest.y - eyes.y;
        double dz = dest.z - eyes.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        mc.player.setYaw((float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90f);
        mc.player.setPitch((float) Math.max(-90, Math.min(90, -(Math.atan2(dy, horiz) * (180 / Math.PI)))));
    }

    public void setGoalAndPath(MinecraftClient mc, Goal goal) {
        this.currentGoal = goal;
        this.mineBlocks.clear();
        this.mineFocus = null;
        this.paused = false;
        this.running = true;
        this.emptyPathStreak = 0;
        repath(mc);
        status = "pathing";
        msg(mc, "§aGoal §f" + goal + " §7(" + executor.remaining() + ")");
    }

    private void repath(MinecraftClient mc) {
        if (currentGoal == null) return;
        List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, SRSettings.maxPathNodes);
        executor.setPath(path);
        lastRepath = System.currentTimeMillis();
        if (path.isEmpty()) {
            status = "no path";
            emptyPathStreak++;
            msg(mc, "§cNo path");
        } else {
            emptyPathStreak = 0;
            status = "path " + path.size();
        }
    }

    public void start(MinecraftClient mc) {
        paused = false;
        running = true;
        if (currentGoal != null && !executor.isActive() && mineBlocks.isEmpty()) repath(mc);
        status = "started";
        msg(mc, "§a#start");
    }

    public void stop(MinecraftClient mc) {
        running = false;
        paused = false;
        mineBlocks.clear();
        mineFocus = null;
        currentGoal = null;
        emptyPathStreak = 0;
        executor.clear();
        try { mc.options.attackKey.setPressed(false); } catch (Exception ignored) {}
        status = "stopped";
        msg(mc, "§c#stop");
    }

    public void pause(MinecraftClient mc) {
        paused = true;
        executor.clear();
        try { mc.options.attackKey.setPressed(false); } catch (Exception ignored) {}
        status = "paused";
        msg(mc, "§e#pause");
    }

    public void resume(MinecraftClient mc) {
        start(mc);
    }

    public void gotoPos(MinecraftClient mc, int x, int y, int z) {
        setGoalAndPath(mc, new GoalBlock(x, y, z));
    }

    public void gotoXZ(MinecraftClient mc, int x, int z) {
        setGoalAndPath(mc, new GoalXZ(x, z));
    }

    public void gotoNear(MinecraftClient mc, int x, int y, int z, int r) {
        setGoalAndPath(mc, new GoalNear(x, y, z, r));
    }

    public void mine(MinecraftClient mc, String blockId) {
        mineBlocks.clear();
        mineFocus = null;
        emptyPathStreak = 0;
        addMineBlock(blockId);
        if (!blockId.contains("deepslate") && blockId.endsWith("_ore")) {
            addMineBlock("deepslate_" + blockId);
        }
        if (blockId.equals("oak_log") || blockId.equals("log") || blockId.equals("logs")) {
            for (String log : List.of("oak_log", "birch_log", "spruce_log", "jungle_log",
                    "acacia_log", "dark_oak_log", "mangrove_log", "cherry_log")) {
                addMineBlock(log);
            }
        }
        if (mineBlocks.isEmpty()) {
            msg(mc, "§cUnknown block: " + blockId);
            return;
        }
        running = true;
        paused = false;
        status = "mine";
        msg(mc, "§a#mine §f" + blockId);
    }

    private void addMineBlock(String id) {
        String full = id.contains(":") ? id : "minecraft:" + id;
        Identifier ident = Identifier.tryParse(full);
        if (ident == null || !Registries.BLOCK.containsId(ident)) return;
        Block b = Registries.BLOCK.get(ident);
        if (b != Blocks.AIR && !mineBlocks.contains(b)) mineBlocks.add(b);
    }

    public boolean isRunning() { return running && !paused; }
    public boolean isPathing() { return executor.isActive(); }
    public String getStatus() { return status; }
    public Goal getGoal() { return currentGoal; }

    private void msg(MinecraftClient mc, String s) {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();
        if (!SRSettings.chatSpam && now - lastChat < 400) return;
        lastChat = now;
        mc.player.sendMessage(Text.literal("§8[§bSRB§8] " + s), false);
    }
}
