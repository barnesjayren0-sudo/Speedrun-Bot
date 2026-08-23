package com.speedrunbot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging for the Speedrun Bot with both console and file output.
 */
public class SpeedrunLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("speedrunbot");
    private static final Path LOG_DIR = Paths.get("speedrunbot_logs");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static Path currentRunLog;

    static {
        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create log directory", e);
        }
    }

    /**
     * Initialize a new run log file.
     */
    public static void initializeRunLog(String runName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            currentRunLog = LOG_DIR.resolve(runName + "_" + timestamp + ".log");
            Files.createFile(currentRunLog);
            log("=== Speedrun Bot Started: " + runName + " ===");
        } catch (IOException e) {
            LOGGER.error("Failed to initialize run log", e);
        }
    }

    /**
     * Log a message to both console and file.
     */
    public static void log(String message) {
        String formatted = "[" + LocalDateTime.now().format(TIME_FORMAT) + "] " + message;
        LOGGER.info(message);
        writeToFile(formatted);
    }

    /**
     * Log a stage transition with clear formatting.
     */
    public static void logStage(String stage, String details) {
        String message = ">>> STAGE: " + stage + (details != null ? " (" + details + ")" : "");
        log(message);
    }

    /**
     * Log debug information (only if debug mode is enabled).
     */
    public static void debug(String message) {
        String formatted = "[DEBUG] " + message;
        LOGGER.debug(message);
        writeToFile(formatted);
    }

    /**
     * Log a warning.
     */
    public static void warn(String message) {
        String formatted = "[WARN] " + message;
        LOGGER.warn(message);
        writeToFile(formatted);
    }

    /**
     * Log an error.
     */
    public static void error(String message, Throwable e) {
        String formatted = "[ERROR] " + message + (e != null ? ": " + e.getMessage() : "");
        LOGGER.error(message, e);
        writeToFile(formatted);
    }

    /**
     * Write a message to the current run log file.
     */
    private static void writeToFile(String message) {
        if (currentRunLog == null) return;
        try {
            Files.writeString(currentRunLog, message + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Failed to write to log file", e);
        }
    }

    /**
     * Log a run summary at the end of a speedrun.
     */
    public static void logRunSummary(long elapsedMillis, boolean successful) {
        long seconds = elapsedMillis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        String status = successful ? "SUCCESS" : "FAILED";
        log("=== Run Complete: " + status + " ===");
        log("Elapsed Time: " + minutes + "m " + seconds + "s");
    }
}
