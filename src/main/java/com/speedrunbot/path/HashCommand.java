package com.speedrunbot.path;

import com.speedrunbot.task.TaskRunner;
import com.speedrunbot.task.tasks.GetItemTask;
import com.speedrunbot.task.tasks.GotoTask;
import com.speedrunbot.task.tasks.MineCountTask;
import net.minecraft.client.MinecraftClient;

public final class HashCommand {

    private HashCommand() {}

    public static boolean tryHandle(String message, SRBaritone srb, TaskRunner tasks) {
        if (message == null || !message.startsWith("#")) return false;
        String body = message.substring(1).trim();
        if (body.isEmpty()) return true;

        String[] args = body.split("\\s+");
        String cmd = args[0].toLowerCase();
        MinecraftClient mc = MinecraftClient.getInstance();

        switch (cmd) {
            case "start" -> srb.start(mc);
            case "stop", "cancel", "forcecancel" -> {
                if (tasks != null) tasks.stop(mc, srb);
                else srb.stop(mc);
            }
            case "pause", "p" -> srb.pause(mc);
            case "resume" -> srb.resume(mc);
            case "goto", "goal" -> handleGoto(args, srb, tasks, mc);
            case "path" -> srb.start(mc);
            case "mine" -> {
                if (args.length < 2) {
                    chat(mc, "§f#mine <block> [count]");
                } else {
                    int n = args.length >= 3 ? parseInt(args[2], 1) : 1;
                    if (tasks != null && n > 1) {
                        tasks.setUserTask(new MineCountTask(args[1], n));
                        chat(mc, "§aTask Mine " + args[1] + " x" + n);
                    } else {
                        srb.mine(mc, args[1]);
                    }
                }
            }
            case "get" -> {
                // AltoClef-style: #get iron 16
                if (args.length < 2) {
                    chat(mc, "§f#get <item> [count]");
                } else {
                    int n = args.length >= 3 ? parseInt(args[2], 1) : 1;
                    if (tasks != null) {
                        tasks.setUserTask(new GetItemTask(args[1], n));
                        chat(mc, "§aTask Get " + args[1] + " x" + n);
                    }
                }
            }
            case "task", "status" -> {
                String t = tasks != null ? tasks.statusLine() : "no tasks";
                chat(mc, "§f" + srb.getStatus() + " §8| §e" + t);
            }
            case "thisway" -> {
                int dist = args.length >= 2 ? parseInt(args[1], 100) : 100;
                float yaw = mc.player.getYaw();
                double rad = Math.toRadians(yaw);
                int x = mc.player.getBlockX() + (int) Math.round(-Math.sin(rad) * dist);
                int z = mc.player.getBlockZ() + (int) Math.round(Math.cos(rad) * dist);
                if (tasks != null) tasks.setUserTask(new GotoTask(x, z));
                else srb.gotoXZ(mc, x, z);
            }
            case "help", "?" -> help(mc);
            default -> chat(mc, "§cUnknown #" + cmd + " — #help");
        }
        return true;
    }

    private static void handleGoto(String[] args, SRBaritone srb, TaskRunner tasks, MinecraftClient mc) {
        if (args.length == 3) {
            int x = parseInt(args[1], 0), z = parseInt(args[2], 0);
            if (tasks != null) tasks.setUserTask(new GotoTask(x, z));
            else srb.gotoXZ(mc, x, z);
        } else if (args.length >= 4) {
            int x = parseInt(args[1], 0), y = parseInt(args[2], 64), z = parseInt(args[3], 0);
            if (tasks != null) tasks.setUserTask(new GotoTask(x, y, z));
            else srb.gotoPos(mc, x, y, z);
        } else {
            chat(mc, "§f#goto <x> <z> | #goto <x> <y> <z>");
        }
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.replace("~", "")); }
        catch (Exception e) { return def; }
    }

    private static void help(MinecraftClient mc) {
        chat(mc, "§bSRBaritone + Tasks (AltoClef-inspired)");
        chat(mc, "§f#start #stop #pause #resume");
        chat(mc, "§f#goto x y z §7| §f#mine iron_ore 8");
        chat(mc, "§f#get iron 16 §7| §f#get diamond 5 §7| §f#get oak_log 12");
        chat(mc, "§f#thisway 80 §7| §f#task §7| §f#help");
    }

    private static void chat(MinecraftClient mc, String s) {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§8[§bSRB§8] " + s), false);
        }
    }
}
