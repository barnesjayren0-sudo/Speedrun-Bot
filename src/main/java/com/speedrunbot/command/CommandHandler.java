package com.speedrunbot.command;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class CommandHandler {

    private CommandHandler() {}

    public static void handle(String message, SpeedrunController controller, BaritoneBridge baritone) {
        String[] args = message.trim().split("\\s+");
        if (args.length < 2) {
            help();
            return;
        }
        switch (args[1].toLowerCase()) {
            case "start" -> controller.start();
            case "stop" -> controller.stop();
            case "skip" -> controller.skip();
            case "status" -> status(controller, baritone);
            case "baritone", "bati" -> {
                baritone.tryHook();
                msg(baritone.isAvailable() ? "§aBaritone HOOKED" : "§cBaritone missing — drop jar in mods/");
            }
            case "goto" -> {
                if (args.length < 3) {
                    msg("§f.sr goto WOOD|IRON|NETHER|...");
                    return;
                }
                try {
                    RunPhase p = RunPhase.valueOf(args[2].toUpperCase());
                    controller.setPhase(p);
                    msg("§aForced phase " + p);
                } catch (Exception e) {
                    msg("§cUnknown phase");
                }
            }
            case "path" -> {
                if (args.length < 5) {
                    msg("§f.sr path <x> <y> <z>");
                    return;
                }
                try {
                    int x = Integer.parseInt(args[2]);
                    int y = Integer.parseInt(args[3]);
                    int z = Integer.parseInt(args[4]);
                    boolean ok = baritone.pathTo(new net.minecraft.util.math.BlockPos(x, y, z));
                    msg(ok ? "§aPathing" : "§cBaritone unavailable");
                } catch (Exception e) {
                    msg("§cBad coords");
                }
            }
            case "cancel" -> {
                baritone.cancel();
                msg("§ePath cancelled");
            }
            case "help" -> help();
            default -> help();
        }
    }

    private static void status(SpeedrunController c, BaritoneBridge b) {
        msg("§fphase=§e" + c.getPhase() + " §frunning=" + c.isRunning()
                + " §fbati=" + (b.isAvailable() ? "§ayes" : "§cno")
                + " §ftime=" + format(c.getRunElapsedMs()));
    }

    private static void help() {
        msg("§f.sr start|stop|skip|status|baritone|goto <PHASE>|path x y z|cancel");
    }

    private static String format(long ms) {
        long s = ms / 1000;
        long m = s / 60;
        s %= 60;
        return m + ":" + (s < 10 ? "0" : "") + s;
    }

    private static void msg(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§8[§6SR§8] " + s), false);
        }
    }
}
