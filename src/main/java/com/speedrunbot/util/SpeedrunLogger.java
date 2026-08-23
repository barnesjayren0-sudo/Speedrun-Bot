package com.speedrunbot.util;

import com.speedrunbot.SpeedrunBotMod;

/** Thin wrapper kept for compatibility with older stubs. */
public final class SpeedrunLogger {

    private SpeedrunLogger() {}

    public static void info(String msg) {
        SpeedrunBotMod.LOGGER.info(msg);
    }

    public static void warn(String msg) {
        SpeedrunBotMod.LOGGER.warn(msg);
    }

    public static void error(String msg, Throwable t) {
        SpeedrunBotMod.LOGGER.error(msg, t);
    }
}
