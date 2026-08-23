package com.speedrunbot.command;

import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.goal.RunPhase;
import com.speedrunbot.path.SRBaritone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class CommandHandler {

    private CommandHandler() {}

    public static void handle(String message, SpeedrunController controller, SRBaritone srb) {
        String[] args = message.trim().split("\\s+");
        if (args.length < 2) {
            help();
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        switch (args[1].toLowerCase()) {
            case "start" -> controller.start();
            case "stop" -> controller.stop();
            case "skip" -> controller.skip();
            case "status" -> msg("§fphase=" + controller.getPhase()
                    + " srb=" + srb.getStatus()
                    + " " + format(controller.getRunElapsedMs()));
            case "goto" -> {
                if (args.length >= 3) {
                    try {
                        controller.setPhase(RunPhase.valueOf(args[2].toUpperCase()));
                        msg("§aPhase " + args[2]);
                    } catch (Exception e) {
                        msg("§cUnknown phase");
                    }
                }
            }
            case "help" -> help();
            default -> help();
        }
    }

    private static void help() {
        msg("§f.sr start|stop|skip|status|goto PHASE");
        msg("§f#start #stop #goto #mine #thisway #help  §7(SRBaritone)");
    }

    private static String format(long ms) {
        long s = ms / 1000;
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }

    private static void msg(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.literal("§8[§6SR§8] " + s), false);
    }
}
