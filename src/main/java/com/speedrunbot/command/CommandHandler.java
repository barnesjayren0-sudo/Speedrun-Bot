package com.speedrunbot.command;

import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.goal.RunPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * Our prefix is .sr — normal Baritone uses # and is handled by Baritone itself.
 * We also accept .sr #... / .sr bati ... to run Baritone commands from code.
 */
public final class CommandHandler {

    private CommandHandler() {}

    public static void handle(String message, SpeedrunController controller, BaritoneBridge baritone) {
        String[] args = message.trim().split("\\s+");
        if (args.length < 2) {
            help();
            return;
        }

        String sub = args[1].toLowerCase();

        // .sr #goto 1 2 3  OR  .sr bati resume  OR  .sr start (baritone resume alias)
        if (sub.startsWith("#") || sub.equals("bati") || sub.equals("b")) {
            String joined;
            if (sub.startsWith("#")) {
                joined = message.substring(message.indexOf('#') + 1).trim();
            } else if (args.length >= 3) {
                joined = message.substring(message.toLowerCase().indexOf(sub) + sub.length()).trim();
            } else {
                msg("§f.sr bati <baritone command>   e.g. .sr bati resume");
                return;
            }
            boolean ok = baritone.run(joined);
            msg(ok ? "§a#" + joined : "§cBaritone missing or command failed");
            return;
        }

        switch (sub) {
            case "start" -> controller.start();
            case "stop" -> controller.stop();
            case "skip" -> controller.skip();
            case "status" -> status(controller, baritone);
            case "baritone", "hook" -> {
                baritone.tryHook();
                msg(baritone.isAvailable()
                        ? "§aBaritone HOOKED — use normal # commands in chat"
                        : "§cPut baritone-fabric-1.21.11.jar in mods/ and restart");
            }
            case "resume" -> {
                boolean ok = baritone.run("resume");
                msg(ok ? "§a#resume" : "§cBaritone unavailable");
            }
            case "goto" -> {
                if (args.length >= 3 && !isInt(args[2])) {
                    try {
                        RunPhase p = RunPhase.valueOf(args[2].toUpperCase());
                        controller.setPhase(p);
                        msg("§aForced phase " + p);
                    } catch (Exception e) {
                        msg("§cUnknown phase");
                    }
                    return;
                }
                if (args.length < 5) {
                    msg("§f.sr path <x> <y> <z>  or  #goto x y z");
                    return;
                }
                pathCoords(args, baritone);
            }
            case "path" -> pathCoords(args, baritone);
            case "cancel" -> {
                baritone.cancel();
                msg("§eCancelled");
            }
            case "help" -> help();
            default -> help();
        }
    }

    private static void pathCoords(String[] args, BaritoneBridge baritone) {
        if (args.length < 5) {
            msg("§f.sr path <x> <y> <z>");
            return;
        }
        try {
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int z = Integer.parseInt(args[4]);
            boolean ok = baritone.pathTo(new BlockPos(x, y, z));
            msg(ok ? "§aPathing" : "§cBaritone unavailable");
        } catch (Exception e) {
            msg("§cBad coords");
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void status(SpeedrunController c, BaritoneBridge b) {
        msg("§fphase=§e" + c.getPhase() + " §frun=" + c.isRunning()
                + " §fbati=" + (b.isAvailable() ? "§ayes" : "§cno")
                + " §fpathing=" + b.isPathing()
                + " §7" + format(c.getRunElapsedMs()));
    }

    private static void help() {
        msg("§6Speedrun: §f.sr start|stop|skip|status|goto PHASE");
        msg("§6Baritone: §f#goto #mine #stop #resume #path #explore  (normal prefix)");
        msg("§7Also: .sr bati resume   .sr resume   .sr path x y z");
        msg("§7Note: Baritone uses §e#resume§7 not #start — we alias start→resume");
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
