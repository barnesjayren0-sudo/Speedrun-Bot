package com.speedrunbot.task;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.chains.FoodChain;
import com.speedrunbot.task.chains.MobDefenseChain;
import com.speedrunbot.task.chains.UnstuckChain;
import net.minecraft.client.MinecraftClient;

/**
 * Runs one user task + background chains (AltoClef-inspired).
 */
public class TaskRunner {

    private Task userTask;
    private final FoodChain food = new FoodChain();
    private final UnstuckChain unstuck = new UnstuckChain();
    private final MobDefenseChain mobs = new MobDefenseChain();

    public void setUserTask(Task task) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (userTask != null) userTask.forceStop(mc, null);
        userTask = task;
    }

    public void stop(MinecraftClient mc, SRBaritone srb) {
        if (userTask != null) {
            userTask.forceStop(mc, srb);
            userTask = null;
        }
        if (srb != null) srb.stop(mc);
    }

    public void tick(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null) return;

        // Background chains first (survival)
        food.tick(mc, srb);
        if (mobs.tickInterrupt(mc, srb)) {
            return; // mob threat handles this tick
        }
        unstuck.tick(mc, srb);

        if (userTask != null) {
            if (userTask.isFinished(mc, srb) || userTask.isStopped()) {
                userTask.forceStop(mc, srb);
                userTask = null;
            } else {
                userTask.tick(mc, srb);
            }
        }
    }

    public Task getUserTask() {
        return userTask;
    }

    public boolean isBusy() {
        return userTask != null && !userTask.isStopped();
    }

    public String statusLine() {
        if (userTask == null) return "idle";
        return userTask.toString();
    }
}
