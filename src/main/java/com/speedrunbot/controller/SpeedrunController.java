package com.speedrunbot.controller;

import com.speedrunbot.SpeedrunBotMod;
import com.speedrunbot.goal.PhaseHandler;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.goal.phases.*;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.Map;

public class SpeedrunController {

    private final SRBaritone srb;
    private final Map<RunPhase, PhaseHandler> handlers = new EnumMap<>(RunPhase.class);

    private boolean running;
    private RunPhase phase = RunPhase.IDLE;
    private PhaseHandler active;
    private long phaseStartedMs;
    private long runStartedMs;

    public SpeedrunController(SRBaritone srb) {
        this.srb = srb;
        register(new WoodPhase());
        register(new StonePhase());
        register(new IronPhase());
        register(new DiamondPhase());
        register(new NetherPhase());
        register(new FortressPhase());
        register(new BlazePhase());
        register(new PearlsPhase());
        register(new StrongholdPhase());
        register(new EndPhase());
    }

    private void register(PhaseHandler h) {
        handlers.put(h.phase(), h);
    }

    public void start() {
        running = true;
        runStartedMs = System.currentTimeMillis();
        setPhase(RunPhase.WOOD);
        msg("§aRun started");
    }

    public void stop() {
        running = false;
        if (active != null) active.exit(MinecraftClient.getInstance(), srb);
        active = null;
        phase = RunPhase.IDLE;
        srb.stop(MinecraftClient.getInstance());
        msg("§cRun stopped");
    }

    public void skip() {
        if (!running) return;
        advance();
    }

    public void setPhase(RunPhase next) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (active != null) active.exit(mc, srb);
        phase = next;
        active = handlers.get(next);
        phaseStartedMs = System.currentTimeMillis();
        if (active != null) {
            active.enter(mc, srb);
            SpeedrunBotMod.LOGGER.info("Phase -> {}", next);
        }
        if (next == RunPhase.DONE) {
            running = false;
            msg("§a§lRUN COMPLETE");
        }
    }

    private void advance() {
        RunPhase n = phase.next();
        msg("§e→ " + n.label);
        setPhase(n);
    }

    public void tick(MinecraftClient mc) {
        if (!running || active == null) return;
        if (phase == RunPhase.DONE || phase == RunPhase.IDLE) return;
        try {
            if (active.tick(mc, srb)) advance();
        } catch (Exception e) {
            SpeedrunBotMod.LOGGER.error("Phase tick error", e);
        }
    }

    public boolean isRunning() { return running; }
    public RunPhase getPhase() { return phase; }
    public long getRunElapsedMs() {
        return runStartedMs == 0 ? 0 : System.currentTimeMillis() - runStartedMs;
    }

    private void msg(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.literal("§8[§6SR§8] " + s), false);
    }
}
