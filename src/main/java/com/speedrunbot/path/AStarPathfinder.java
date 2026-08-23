package com.speedrunbot.path;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Lightweight A* on a 3D grid — good enough for short/mid range goals.
 * Not full Baritone, but our own and under our control.
 */
public final class AStarPathfinder {

    private static final int MAX_NODES = 25000;
    private static final int[][] DIRS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
            {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
            {0, 1, 0}, {0, -1, 0},
            {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
            {1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1},
    };

    private AStarPathfinder() {}

    public static List<BlockPos> find(MinecraftClient mc, Goal goal, int maxNodes) {
        if (mc.player == null || mc.world == null || goal == null) return List.of();

        World world = mc.world;
        BlockPos start = mc.player.getBlockPos();
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
        int limit = Math.min(maxNodes, MAX_NODES);
        PathNode best = root;

        while (!open.isEmpty() && expanded < limit) {
            PathNode cur = open.poll();
            if (closed.contains(cur.key())) continue;
            closed.add(cur.key());
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

                if (ny < world.getBottomY() || ny >= world.getTopYInclusive()) continue;
                if (!isWalkable(world, nx, ny, nz)) continue;

                long key = BlockKeys.pack(nx, ny, nz);
                if (closed.contains(key)) continue;

                double step = (d[0] != 0 && d[2] != 0) ? 1.414 : 1.0;
                if (d[1] != 0) step += 0.5;
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
                next.f = ng + goal.heuristic(nx, ny, nz);
                open.add(next);
            }
        }

        // Partial path toward closest approach
        if (best != root) return reconstruct(best);
        return List.of();
    }

    private static List<BlockPos> reconstruct(PathNode end) {
        LinkedList<BlockPos> path = new LinkedList<>();
        PathNode n = end;
        while (n != null) {
            path.addFirst(new BlockPos(n.x, n.y, n.z));
            n = n.parent;
        }
        return path;
    }

    /** Feet at (x,y,z): block below solid-ish, feet+head not solid. */
    public static boolean isWalkable(World world, int x, int y, int z) {
        BlockPos feet = new BlockPos(x, y, z);
        BlockPos head = feet.up();
        BlockPos below = feet.down();

        BlockState feetState = world.getBlockState(feet);
        BlockState headState = world.getBlockState(head);
        BlockState belowState = world.getBlockState(below);

        if (isSolid(feetState) || isSolid(headState)) return false;
        // need ground OR water swim
        if (isSolid(belowState)) return true;
        if (feetState.isOf(Blocks.WATER) || feetState.isOf(Blocks.LAVA)) return true;
        // step-up from slab-ish: allow if below is not air and not dangerous
        return !belowState.isAir() && !belowState.isOf(Blocks.LAVA);
    }

    private static boolean isSolid(BlockState s) {
        if (s.isAir()) return false;
        if (s.isOf(Blocks.WATER) || s.isOf(Blocks.LAVA) || s.isOf(Blocks.SHORT_GRASS)
                || s.isOf(Blocks.TALL_GRASS) || s.isOf(Blocks.SNOW)) return false;
        return s.isSolidBlock(null, BlockPos.ORIGIN) || s.blocksMovement();
    }
}
