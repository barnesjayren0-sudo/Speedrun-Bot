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
    private static int hookRetry;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Speedrun-Bot client v{} (1.21.11)", SpeedrunBotMod.VERSION);

        baritone = new BaritoneBridge();
        baritone.tryHook();

        controller = new SpeedrunController(baritone);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Baritone may load after us — retry hook a few times
            if (!baritone.isAvailable() && hookRetry < 200 && client.player != null) {
                if (hookRetry % 40 == 0) baritone.tryHook();
                hookRetry++;
            }
            if (client.player == null || client.world == null) return;
            controller.tick(client);
        });

        // ONLY intercept .sr — leave # alone so normal Baritone chat control works
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith(".sr") || message.startsWith(".speedrun")) {
                CommandHandler.handle(message, controller, baritone);
                return false;
            }
            // #goto #mine #resume #stop etc → Baritone ExampleBaritoneControl
            return true;
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            SpeedrunHud.render(context, controller, baritone);
        });

        LOGGER.info("Baritone: {} | use # commands in chat for normal Baritone", 
                baritone.isAvailable() ? "HOOKED" : "install baritone-fabric-1.21.11.jar in mods/");
    }

    public static SpeedrunController getController() {
        return controller;
    }

    public static BaritoneBridge getBaritone() {
        return baritone;
    }
}
