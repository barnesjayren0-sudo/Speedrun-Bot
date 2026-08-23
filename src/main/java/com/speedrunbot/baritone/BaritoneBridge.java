package com.speedrunbot.baritone;

import com.speedrunbot.SpeedrunBotMod;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflective hook into official Baritone Fabric (1.21.11 jar).
 * Install baritone-fabric-1.21.11.jar in mods/ — all normal # commands work from Baritone itself.
 * This bridge is for the speedrun pipeline to call path/mine/cancel in code.
 */
public class BaritoneBridge {

    private static final Logger LOG = SpeedrunBotMod.LOGGER;

    private boolean available;
    private Object primaryBaritone;
    private Object customGoalProcess;
    private Object commandManager;
    private Method setGoalAndPath;
    private Method cmdExecute;

    public void tryHook() {
        available = false;
        primaryBaritone = null;
        try {
            Class<?> api = Class.forName("baritone.api.BaritoneAPI");
            Object provider = api.getMethod("getProvider").invoke(null);
            primaryBaritone = provider.getClass().getMethod("getPrimaryBaritone").invoke(provider);

            customGoalProcess = primaryBaritone.getClass()
                    .getMethod("getCustomGoalProcess").invoke(primaryBaritone);

            for (Method m : customGoalProcess.getClass().getMethods()) {
                if (m.getName().equals("setGoalAndPath") && m.getParameterCount() == 1) {
                    setGoalAndPath = m;
                    break;
                }
            }

            commandManager = primaryBaritone.getClass()
                    .getMethod("getCommandManager").invoke(primaryBaritone);
            for (Method m : commandManager.getClass().getMethods()) {
                if (m.getName().equals("execute")) {
                    cmdExecute = m;
                    break;
                }
            }

            available = primaryBaritone != null;
            LOG.info("[BaritoneBridge] hooked (Baritone present)");
        } catch (Throwable t) {
            available = false;
            LOG.info("[BaritoneBridge] Baritone not in mods yet: {}", t.getMessage());
        }
    }

    public boolean isAvailable() {
        return available;
    }

    /**
 * Run a normal Baritone command string (without #).
 * Examples: "goto 100 64 200", "mine diamond_ore", "stop", "resume", "path"
 */
    public boolean run(String commandWithoutHash) {
        if (!available || commandManager == null || cmdExecute == null) return false;
        try {
            String cmd = commandWithoutHash.startsWith("#")
                    ? commandWithoutHash.substring(1).trim()
                    : commandWithoutHash.trim();
            // Alias: start → resume (Baritone has resume, not start)
            if (cmd.equalsIgnoreCase("start")) {
                cmd = "resume";
            }
            Class<?>[] pts = cmdExecute.getParameterTypes();
            if (pts.length == 1 && pts[0] == String.class) {
                cmdExecute.invoke(commandManager, cmd);
                return true;
            }
            if (pts.length >= 1) {
                Object[] args = new Object[pts.length];
                args[0] = cmd;
                cmdExecute.invoke(commandManager, args);
                return true;
            }
            return false;
        } catch (Throwable t) {
            LOG.warn("[BaritoneBridge] run('{}') failed: {}", commandWithoutHash, t.toString());
            return false;
        }
    }

    public boolean pathTo(BlockPos pos) {
        if (!available || pos == null) return false;
        // Prefer chat-level goto so it behaves exactly like #goto
        if (run("goto " + pos.getX() + " " + pos.getY() + " " + pos.getZ())) {
            return true;
        }
        try {
            Class<?> goalBlock = Class.forName("baritone.api.pathing.goals.GoalBlock");
            Object goal = goalBlock.getConstructor(int.class, int.class, int.class)
                    .newInstance(pos.getX(), pos.getY(), pos.getZ());
            if (setGoalAndPath != null) {
                setGoalAndPath.invoke(customGoalProcess, goal);
                return true;
            }
        } catch (Throwable t) {
            LOG.warn("[BaritoneBridge] pathTo failed: {}", t.toString());
        }
        return false;
    }

    public boolean pathToXZ(int x, int z) {
        return run("goto " + x + " " + z) || run("goto " + x + " ~ " + z);
    }

    public void cancel() {
        run("stop");
        run("cancel");
    }

    public boolean isPathing() {
        if (!available || primaryBaritone == null) return false;
        try {
            Object pb = primaryBaritone.getClass().getMethod("getPathingBehavior").invoke(primaryBaritone);
            Object r = pb.getClass().getMethod("isPathing").invoke(pb);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable t) {
            return false;
        }
    }

    /** @deprecated use {@link #run(String)} */
    public boolean chatCommand(String cmd) {
        // strip leading #mine style if our phases pass "mine iron_ore"
        return run(cmd);
    }
}
