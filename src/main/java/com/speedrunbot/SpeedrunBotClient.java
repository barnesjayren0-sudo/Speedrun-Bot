package com.speedrunbot;

import com.speedrunbot.command.CommandHandler;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.hud.SpeedrunHud;
import com.speedrunbot.path.HashCommand;
import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.TaskRunner;
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
    private static TaskRunner tasks;
    private static SpeedrunController controller;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Speedrun-Bot v{} (tasks + SRBaritone)", SpeedrunBotMod.VERSION);

        srb = new SRBaritone();
        tasks = new TaskRunner();
        controller = new SpeedrunController(srb);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            tasks.tick(client, srb);
            // Only tick raw path executor if no task is driving mine/goto via SRBaritone
            srb.tick(client);
            controller.tick(client);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith("#")) {
                HashCommand.tryHandle(message, srb, tasks);
                return false;
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
            SpeedrunHud.render(context, controller, srb, tasks);
        });
    }

    public static SRBaritone getSrb() { return srb; }
    public static TaskRunner getTasks() { return tasks; }
    public static SpeedrunController getController() { return controller; }
}
