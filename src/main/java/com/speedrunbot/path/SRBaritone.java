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

/** Custom pathfinder controller for MC 1.21.11 — #start #goto #mine */
public class SRBaritone {

    private final PathExecutor executor = new PathExecutor();
    private Goal currentGoal;
    private boolean running;
    private boolean paused;
    private String status = "idle";
    private final List<Block> mineBlocks = new ArrayList<>();
    private long lastRepath;
    private BlockPos mineFocus;

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        if (!running || paused) return;

        if (!mineBlocks.isEmpty()) {
            tickMine(mc);
            return;
        }

        if (!executor.isActive() && currentGoal != null) {
            if (currentGoal.isInGoal(mc.player.getBlockPos())) {
                status = "arrived";
                running = false;
                msg(mc, "§aArrived");
                return;
            }
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
            status = "mine: none in range";
            executor.clear();
            return;
        }

        double dist = mc.player.getEyePos().distanceTo(Vec3d.ofCenter(mineFocus));
        if (dist <= 4.5) {
            status = "mine: dig";
            executor.clear();
            lookAt(mc, mineFocus);
            mc.options.attackKey.setPressed(true);
            if (mc.world.getBlockState(mineFocus).isAir()) {
                mc.options.attackKey.setPressed(false);
                mineFocus = null;
            }
            return;
        }

        try {
            mc.options.attackKey.setPressed(false);
        } catch (Exception ignored) {}

        if (!executor.isActive() || System.currentTimeMillis() - lastRepath > SRSettings.repathMs) {
            currentGoal = new GoalBlock(mineFocus);
            List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, SRSettings.maxPathNodes);
            // path near the block, not inside it
            if (!path.isEmpty()) {
                path = new ArrayList<>(path);
                // stop adjacent
                BlockPos last = path.get(path.size() - 1);
                if (last.equals(mineFocus) && path.size() > 1) {
                    path.remove(path.size() - 1);
                }
            }
            executor.setPath(path);
            lastRepath = System.currentTimeMillis();
            status = "mine: path " + path.size();
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
        int r = Math.min(range, 40);
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -20; dy <= 20; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    int x = origin.getX() + dx;
                    int y = origin.getY() + dy;
                    int z = origin.getZ() + dz;
                    if (y < mc.world.getBottomY() || y >= mc.world.getBottomY() + mc.world.getHeight()) continue;
                    m.set(x, y, z);
                    BlockState st = mc.world.getBlockState(m);
                    if (!matchesMine(st)) continue;
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
        float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90f;
        float pitch = (float) (-(Math.atan2(dy, horiz) * (180 / Math.PI)));
        mc.player.setYaw(yaw);
        mc.player.setPitch(Math.max(-90, Math.min(90, pitch)));
    }

    public void setGoalAndPath(MinecraftClient mc, Goal goal) {
        this.currentGoal = goal;
        this.mineBlocks.clear();
        this.mineFocus = null;
        this.paused = false;
        this.running = true;
        repath(mc);
        status = "pathing";
        msg(mc, "§aGoal §f" + goal + " §7(" + executor.remaining() + " nodes)");
    }

    private void repath(MinecraftClient mc) {
        if (currentGoal == null) return;
        List<BlockPos> path = AStarPathfinder.find(mc, currentGoal, SRSettings.maxPathNodes);
        executor.setPath(path);
        lastRepath = System.currentTimeMillis();
        if (path.isEmpty()) {
            status = "no path";
            msg(mc, "§cNo path");
        } else {
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
        try {
            mc.options.attackKey.setPressed(false);
        } catch (Exception ignored) {}
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

    public void mine(MinecraftClient mc, String blockId) {
        mineBlocks.clear();
        mineFocus = null;
        addMineBlock(blockId);
        // 1.21 deepslate variants
        if (!blockId.contains("deepslate") && blockId.endsWith("_ore")) {
            addMineBlock("deepslate_" + blockId);
        }
        if (blockId.equals("iron_ore")) addMineBlock("deepslate_iron_ore");
        if (blockId.equals("diamond_ore")) addMineBlock("deepslate_diamond_ore");
        if (blockId.equals("gold_ore")) addMineBlock("deepslate_gold_ore");
        if (blockId.equals("coal_ore")) addMineBlock("deepslate_coal_ore");
        if (blockId.equals("oak_log")) {
            addMineBlock("birch_log");
            addMineBlock("spruce_log");
            addMineBlock("jungle_log");
            addMineBlock("acacia_log");
            addMineBlock("dark_oak_log");
            addMineBlock("mangrove_log");
            addMineBlock("cherry_log");
        }

        if (mineBlocks.isEmpty()) {
            msg(mc, "§cUnknown block: " + blockId);
            return;
        }
        running = true;
        paused = false;
        status = "mine";
        msg(mc, "§a#mine §f" + blockId + " §7(+variants)");
    }

    private void addMineBlock(String id) {
        String full = id.contains(":") ? id : "minecraft:" + id;
        Identifier ident = Identifier.tryParse(full);
        if (ident == null) return;
        if (!Registries.BLOCK.containsId(ident)) return;
        Block b = Registries.BLOCK.get(ident);
        if (b != Blocks.AIR && !mineBlocks.contains(b)) mineBlocks.add(b);
    }

    public boolean isRunning() { return running && !paused; }
    public boolean isPathing() { return executor.isActive(); }
    public String getStatus() { return status; }
    public Goal getGoal() { return currentGoal; }

    private void msg(MinecraftClient mc, String s) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§8[§bSRB§8] " + s), false);
        }
    }
}
