package com.speedrunbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Speedrun Bot client-side entry point and tick event handling.
 */
public class SpeedrunBotClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedrunBotMod.MOD_ID);
    private static SpeedrunController speedrunController;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Speedrun Bot client initializing...");
        
        // Initialize the main speedrun controller
        speedrunController = new SpeedrunController();
        
        // Register client tick event to update bot state every tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                speedrunController.tick(client);
            }
        });
        
        LOGGER.info("Speedrun Bot client initialized successfully");
    }

    public static SpeedrunController getController() {
        return speedrunController;
    }
}
