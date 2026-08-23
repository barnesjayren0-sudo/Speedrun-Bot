package com.speedrunbot.task;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.path.SRSettings;
import net.minecraft.client.MinecraftClient;

public abstract class Task {

    private Task sub;
    private boolean started;
    private boolean stopped;
    private long startedAt;

    public final void tick(MinecraftClient mc, SRBaritone srb) {
        if (stopped) return;

        if (!started) {
            started = true;
            startedAt = System.currentTimeMillis();
            onStart(mc, srb);
        }

        if (SRSettings.taskTimeoutMs > 0
                && System.currentTimeMillis() - startedAt > SRSettings.taskTimeoutMs) {
            onStop(mc, srb);
            return;
        }

        if (isFinished(mc, srb)) {
            if (sub != null) {
                sub.onStop(mc, srb);
                sub = null;
            }
            return;
        }

        if (sub != null) {
            if (sub.isFinished(mc, srb) || sub.isStopped()) {
                sub.onStop(mc, srb);
                sub = null;
            } else {
                sub.tick(mc, srb);
                return;
            }
        }

        Task next = onTick(mc, srb);
        if (next != null && next != this && next != sub) {
            sub = next;
            if (!sub.isFinished(mc, srb)) {
                sub.tick(mc, srb);
            } else {
                sub.onStop(mc, srb);
                sub = null;
            }
        }
    }

    protected void onStart(MinecraftClient mc, SRBaritone srb) {}

    protected abstract Task onTick(MinecraftClient mc, SRBaritone srb);

    public abstract boolean isFinished(MinecraftClient mc, SRBaritone srb);

    protected void onStop(MinecraftClient mc, SRBaritone srb) {
        if (stopped) return;
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
        return sub != null ? getName() + " → " + sub : getName();
    }
}
