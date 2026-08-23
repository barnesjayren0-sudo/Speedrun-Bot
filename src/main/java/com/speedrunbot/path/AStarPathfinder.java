package com.speedrunbot.path;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * A* pathfinder tuned for Minecraft 1.21.11 client worlds.
 */
public final class AStarPathfinder {

    private static final int[][] DIRS = {
            // cardinal
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
            // diagonal ground
            {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
            // up 1 (step / jump)
            {0, 1, 0},
            {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
            // down 1
            {0, -1, 0},
            {1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1},
            // down 2–3 (safe-ish drops)
            {0, -2, 0}, {1, -2, 0}, {-1, -2, 0}, {0, -2, 1}, {0, -2, -1},
            {0, -3, 0},
    };

    private AStarPathfinder() {}

    public static List<BlockPos> find(MinecraftClient mc, Goal goal, int maxNodes) {
        if (mc.player == null || mc.world == null || goal == null) return List.of();

        World world = mc.world;
        BlockPos start = BlockPos.ofFloored(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        int sx = start.getX(), sy = start.getY(), sz = start.getZ();
        if (goal.isInGoal(sx, sy, sz)) return List.of(start);

        PriorityQueue<PathNode> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<Long, PathNode> all = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        PathNode root = new PathNode(sx, sy, sz);
        root.g = 0;
        root.f = goal.heuristic(sx, sy, sz);
        open.add(root);
        all.put(root.key(), root);

        int expanded = 0;
        int limit = Math.min(maxNodes, SRSettings.maxPathNodes);
        PathNode best = root;
        int bottom = world.getBottomY();
        int top = bottom + world.getHeight();

        while (!open.isEmpty() && expanded < limit) {
            PathNode cur = open.poll();
            if (!closed.add(cur.key())) continue;
            expanded++;

            if (goal.heuristic(cur.x, cur.y, cur.z) < goal.heuristic(best.x, best.y, best.z)) {
                best = cur;
            }
            if (goal.isInGoal(cur.x, cur.y, cur.z)) {
                return reconstruct(cur);
            }

            for (int[] d : DIRS) {
                int nx = cur.x + d[0];
                int ny = cur.y + d[1];
                int nz = cur.z + d[2];
                if (ny < bottom + 1 || ny >= top - 1) continue;
                if (!isWalkable(world, nx, ny, nz)) continue;
                // don't fall more than 3 without path support
                if (d[1] <= -2 && !hasGroundNearby(world, nx, ny, nz)) continue;

                long key = BlockKeys.pack(nx, ny, nz);
                if (closed.contains(key)) continue;

                double step = cost(d);
                double ng = cur.g + step;

                PathNode next = all.get(key);
                if (next == null) {
                    next = new PathNode(nx, ny, nz);
                    all.put(key, next);
                } else if (ng >= next.g) {
                    continue;
                }
                next.parent = cur;
                next.g = ng;
                next.f = ng + goal.heuristic(nx, ny, nz) * 1.001;
                open.add(next);
            }
        }
        return best != root ? reconstruct(best) : List.of();
    }

    private static double cost(int[] d) {
        double c = 1.0;
        if (d[0] != 0 && d[2] != 0) c = 1.414;
        if (d[1] > 0) c += 1.2;
        if (d[1] < 0) c += 0.3 * (-d[1]);
        return c;
    }

    private static boolean hasGroundNearby(World world, int x, int y, int z) {
        return solidGround(world.getBlockState(new BlockPos(x, y - 1, z)));
    }

    private static List<BlockPos> reconstruct(PathNode end) {
        LinkedList<BlockPos> path = new LinkedList<>();
        for (PathNode n = end; n != null; n = n.parent) {
            path.addFirst(new BlockPos(n.x, n.y, n.z));
        }
        return path;
    }

    /** Player feet can stand at (x,y,z). */
    public static boolean isWalkable(World world, int x, int y, int z) {
        BlockPos feet = new BlockPos(x, y, z);
        BlockPos head = feet.up();
        BlockPos below = feet.down();

        BlockState atFeet = world.getBlockState(feet);
        BlockState atHead = world.getBlockState(head);
        BlockState atBelow = world.getBlockState(below);

        if (isPassable(atFeet) && isPassable(atHead) && solidGround(atBelow)) return true;
        // swimming
        return atFeet.isOf(Blocks.WATER) && isPassable(atHead);
    }

    private static boolean isPassable(BlockState s) {
        if (s.isAir()) return true;
        if (s.isOf(Blocks.WATER)) return true;
        if (s.isOf(Blocks.SHORT_GRASS) || s.isOf(Blocks.TALL_GRASS)) return true;
        if (s.isOf(Blocks.SNOW) || s.isOf(Blocks.FERN) || s.isOf(Blocks.LARGE_FERN)) return true;
        if (s.isOf(Blocks.TORCH) || s.isOf(Blocks.WALL_TORCH) || s.isOf(Blocks.LANTERN)) return true;
        if (s.isOf(Blocks.COBWEB)) return true;
        return !s.blocksMovement();
    }

    private static boolean solidGround(BlockState s) {
        if (s.isAir() || s.isOf(Blocks.LAVA) || s.isOf(Blocks.WATER)) return false;
        if (s.isOf(Blocks.SHORT_GRASS) || s.isOf(Blocks.TALL_GRASS) || s.isOf(Blocks.SNOW)) return false;
        return s.blocksMovement();
    }
}
