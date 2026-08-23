package com.speedrunbot.task;

import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

/**
 * AltoClef-style task: can spawn a subtask, finishes when isFinished().
 */
public abstract class Task {

    private Task sub;
    private boolean started;
    private boolean stopped;

    public final void tick(MinecraftClient mc, SRBaritone srb) {
        if (stopped) return;
        if (!started) {
            started = true;
            onStart(mc, srb);
        }
        if (sub != null) {
            if (sub.isFinished(mc, srb) || sub.stopped) {
                sub.onStop(mc, srb);
                sub = null;
            } else {
                sub.tick(mc, srb);
                return;
            }
        }
        Task next = onTick(mc, srb);
        if (next != null && next != this) {
            if (sub != null) sub.onStop(mc, srb);
            sub = next;
        }
    }

    protected void onStart(MinecraftClient mc, SRBaritone srb) {}

    /** @return optional subtask to run first */
    protected abstract Task onTick(MinecraftClient mc, SRBaritone srb);

    public abstract boolean isFinished(MinecraftClient mc, SRBaritone srb);

    protected void onStop(MinecraftClient mc, SRBaritone srb) {
        stopped = true;
        if (sub != null) {
            sub.onStop(mc, srb);
            sub = null;
        }
    }

    public void forceStop(MinecraftClient mc, SRBaritone srb) {
        onStop(mc, srb);
    }

    public boolean isStopped() {
        return stopped;
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName() + (sub != null ? " → " + sub : "");
    }
}
