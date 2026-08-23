package com.speedrunbot.path;

import net.minecraft.client.MinecraftClient;

/**
 * Parses normal Baritone-style # commands for SRBaritone.
 */
public final class HashCommand {

    private HashCommand() {}

    /** @return true if message was a # command and was handled */
    public static boolean tryHandle(String message, SRBaritone srb) {
        if (message == null || !message.startsWith("#")) return false;
        String body = message.substring(1).trim();
        if (body.isEmpty()) return true;

        String[] args = body.split("\\s+");
        String cmd = args[0].toLowerCase();
        MinecraftClient mc = MinecraftClient.getInstance();

        switch (cmd) {
            case "start" -> srb.start(mc);
            case "stop", "cancel", "forcecancel" -> srb.stop(mc);
            case "pause", "p" -> srb.pause(mc);
            case "resume" -> srb.resume(mc);
            case "goto", "goal" -> handleGoto(args, srb, mc);
            case "path" -> {
                if (srb.getGoal() != null) srb.start(mc);
                else srb.start(mc);
            }
            case "mine" -> {
                if (args.length < 2) {
                    chat(mc, "§f#mine <block>  e.g. #mine iron_ore");
                } else {
                    srb.mine(mc, args[1]);
                }
            }
            case "thisway" -> {
                int dist = args.length >= 2 ? parseInt(args[1], 100) : 100;
                float yaw = mc.player.getYaw();
                double rad = Math.toRadians(yaw);
                int x = mc.player.getBlockX() + (int) Math.round(-Math.sin(rad) * dist);
                int z = mc.player.getBlockZ() + (int) Math.round(Math.cos(rad) * dist);
                srb.gotoXZ(mc, x, z);
            }
            case "come" -> {
                // stay put goal = current pos (noop) / stop
                srb.stop(mc);
            }
            case "help", "?" -> help(mc);
            case "status" -> chat(mc, "§f" + srb.getStatus()
                    + " running=" + srb.isRunning()
                    + " pathing=" + srb.isPathing()
                    + " goal=" + srb.getGoal());
            default -> chat(mc, "§cUnknown #" + cmd + " — try #help");
        }
        return true;
    }

    private static void handleGoto(String[] args, SRBaritone srb, MinecraftClient mc) {
        if (args.length == 3) {
            srb.gotoXZ(mc, parseInt(args[1], 0), parseInt(args[2], 0));
        } else if (args.length >= 4) {
            srb.gotoPos(mc, parseInt(args[1], 0), parseInt(args[2], 64), parseInt(args[3], 0));
        } else {
            chat(mc, "§f#goto <x> <z>  or  #goto <x> <y> <z>");
        }
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.replace("~", ""));
        } catch (Exception e) {
            return def;
        }
    }

    private static void help(MinecraftClient mc) {
        chat(mc, "§bSRBaritone commands:");
        chat(mc, "§f#start §7— start/resume path");
        chat(mc, "§f#stop §7— stop");
        chat(mc, "§f#pause §7/ §f#resume");
        chat(mc, "§f#goto x y z §7/ §f#goto x z");
        chat(mc, "§f#mine iron_ore");
        chat(mc, "§f#thisway [dist] §7— walk forward");
        chat(mc, "§f#status §7/ §f#help");
    }

    private static void chat(MinecraftClient mc, String s) {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§8[§bSRB§8] " + s), false);
        }
    }
}
