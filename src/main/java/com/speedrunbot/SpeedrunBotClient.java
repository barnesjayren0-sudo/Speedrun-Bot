package com.speedrunbot;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.command.CommandHandler;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.hud.SpeedrunHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedrunBotClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedrunBotMod.MOD_ID);

    private static SpeedrunController controller;
    private static BaritoneBridge baritone;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Speedrun-Bot client v{}", SpeedrunBotMod.VERSION);

        baritone = new BaritoneBridge();
        baritone.tryHook();

        controller = new SpeedrunController(baritone);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            controller.tick(client);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith(".sr") || message.startsWith(".speedrun")) {
                CommandHandler.handle(message, controller, baritone);
                return false;
            }
            return true;
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            SpeedrunHud.render(context, controller, baritone);
        });

        LOGGER.info("Baritone bridge: {}", baritone.isAvailable() ? "HOOKED" : "WAITING (drop jar later)");
    }

    public static SpeedrunController getController() {
        return controller;
    }

    public static BaritoneBridge getBaritone() {
        return baritone;
    }
}
