package com.speedrunbot.goal;

import com.speedrunbot.baritone.BaritoneBridge;
import net.minecraft.client.MinecraftClient;

public interface PhaseHandler {
    RunPhase phase();

    /** Start actions for this phase (path, #mine, etc). */
    void enter(MinecraftClient mc, BaritoneBridge baritone);

    /** @return true when phase goals are satisfied and bot should advance. */
    boolean tick(MinecraftClient mc, BaritoneBridge baritone);

    default void exit(MinecraftClient mc, BaritoneBridge baritone) {}
}
