package com.speedrunbot.baritone;

import com.speedrunbot.SpeedrunBotMod;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflective Baritone hook so the mod compiles without the Baritone jar.
 * When you add baritone (API + standalone) to mods/ or libs/, this auto-detects it.
 *
 * Later: replace reflection with real imports once your custom Baritone is in-repo.
 */
public class BaritoneBridge {

    private static final Logger LOG = SpeedrunBotMod.LOGGER;

    private boolean available;
    private Object primaryBaritone;
    private Object customGoalProcess;
    private Method setGoalAndPath;
    private Method cancelEverything;
    private Method isPathing;

    public void tryHook() {
        available = false;
        try {
            Class<?> api = Class.forName("baritone.api.BaritoneAPI");
            Method getProvider = api.getMethod("getProvider");
            Object provider = getProvider.invoke(null);
            Method getPrimary = provider.getClass().getMethod("getPrimaryBaritone");
            primaryBaritone = getPrimary.invoke(provider);

            Method getCustomGoal = primaryBaritone.getClass().getMethod("getCustomGoalProcess");
            customGoalProcess = getCustomGoal.invoke(primaryBaritone);

            // setGoalAndPath(Goal)
            for (Method m : customGoalProcess.getClass().getMethods()) {
                if (m.getName().equals("setGoalAndPath") && m.getParameterCount() == 1) {
                    setGoalAndPath = m;
                    break;
                }
            }

            Object pathingBehavior = primaryBaritone.getClass()
                    .getMethod("getPathingBehavior").invoke(primaryBaritone);
            cancelEverything = pathingBehavior.getClass().getMethod("cancelEverything");
            isPathing = pathingBehavior.getClass().getMethod("isPathing");

            available = setGoalAndPath != null;
            LOG.info("[BaritoneBridge] hooked successfully");
        } catch (Throwable t) {
            available = false;
            LOG.info("[BaritoneBridge] Baritone not found yet — install jar or supply custom build");
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean pathTo(BlockPos pos) {
        if (!available || pos == null) return false;
        try {
            Class<?> goalXZ = Class.forName("baritone.api.pathing.goals.GoalBlock");
            Object goal = goalXZ.getConstructor(int.class, int.class, int.class)
                    .newInstance(pos.getX(), pos.getY(), pos.getZ());
            setGoalAndPath.invoke(customGoalProcess, goal);
            return true;
        } catch (Throwable t) {
            LOG.warn("[BaritoneBridge] pathTo failed: {}", t.toString());
            return false;
        }
    }

    public boolean pathToXZ(int x, int z) {
        if (!available) return false;
        try {
            Class<?> goalXZ = Class.forName("baritone.api.pathing.goals.GoalXZ");
            Object goal = goalXZ.getConstructor(int.class, int.class).newInstance(x, z);
            setGoalAndPath.invoke(customGoalProcess, goal);
            return true;
        } catch (Throwable t) {
            LOG.warn("[BaritoneBridge] pathToXZ failed: {}", t.toString());
            return false;
        }
    }

    public void cancel() {
        if (!available || cancelEverything == null) return;
        try {
            cancelEverything.invoke(
                    primaryBaritone.getClass().getMethod("getPathingBehavior").invoke(primaryBaritone)
            );
        } catch (Throwable t) {
            LOG.warn("[BaritoneBridge] cancel failed: {}", t.toString());
        }
    }

    public boolean isPathing() {
        if (!available || isPathing == null) return false;
        try {
            Object pathingBehavior = primaryBaritone.getClass()
                    .getMethod("getPathingBehavior").invoke(primaryBaritone);
            Object r = isPathing.invoke(pathingBehavior);
            return r instanceof Boolean && (Boolean) r;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Run a raw Baritone chat command if present (#mine iron_ore etc). */
    public boolean chatCommand(String cmd) {
        if (!available) return false;
        try {
            Object cmdMgr = primaryBaritone.getClass().getMethod("getCommandManager").invoke(primaryBaritone);
            Method execute = null;
            for (Method m : cmdMgr.getClass().getMethods()) {
                if (m.getName().equals("execute") && m.getParameterCount() >= 1) {
                    execute = m;
                    break;
                }
            }
            if (execute == null) return false;
            if (execute.getParameterCount() == 1) {
                execute.invoke(cmdMgr, cmd);
            } else {
                execute.invoke(cmdMgr, cmd, null);
            }
            return true;
        } catch (Throwable t) {
            // fallback: many Baritone builds listen to chat with #
            return false;
        }
    }
}
