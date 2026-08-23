package com.speedrunbot;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedrunBotMod implements ModInitializer {

    public static final String MOD_ID = "speedrunbot";
    public static final String VERSION = "1.5.1";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Speedrun-Bot {} (polished) — MC 1.21.11", VERSION);
    }
}
