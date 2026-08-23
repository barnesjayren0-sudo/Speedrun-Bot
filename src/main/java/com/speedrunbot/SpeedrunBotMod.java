package com.speedrunbot;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Speedrun Bot main mod entry point for server/common initialization.
 */
public class SpeedrunBotMod implements ModInitializer {
    public static final String MOD_ID = "speedrunbot";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Speedrun Bot initializing...");
    }
}
