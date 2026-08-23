package com.speedrunbot.goal;

import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;

public interface PhaseHandler {
    RunPhase phase();
    void enter(MinecraftClient mc, SRBaritone srb);
    boolean tick(MinecraftClient mc, SRBaritone srb);
    default void exit(MinecraftClient mc, SRBaritone srb) {}
}
