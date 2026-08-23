package com.speedrunbot;

import com.speedrunbot.command.CommandHandler;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.hud.SpeedrunHud;
import com.speedrunbot.path.HashCommand;
import com.speedrunbot.path.SRBaritone;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedrunBotClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedrunBotMod.MOD_ID);

    private static SRBaritone srb;
    private static SpeedrunController controller;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Speedrun-Bot v{} — SRBaritone (custom # commands)", SpeedrunBotMod.VERSION);

        srb = new SRBaritone();
        controller = new SpeedrunController(srb);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            srb.tick(client);
            controller.tick(client);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            // Our own Baritone-style #
            if (message.startsWith("#")) {
                HashCommand.tryHandle(message, srb);
                return false; // don't send to server
            }
            if (message.startsWith(".sr") || message.startsWith(".speedrun")) {
                CommandHandler.handle(message, controller, srb);
                return false;
            }
            return true;
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            SpeedrunHud.render(context, controller, srb);
        });
    }

    public static SRBaritone getSrb() {
        return srb;
    }

    public static SpeedrunController getController() {
        return controller;
    }
}
