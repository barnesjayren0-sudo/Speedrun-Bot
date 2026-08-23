package com.speedrunbot.hud;

import com.speedrunbot.SpeedrunBotMod;
import com.speedrunbot.controller.SpeedrunController;
import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.TaskRunner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class SpeedrunHud {

    private SpeedrunHud() {}

    public static void render(DrawContext ctx, SpeedrunController controller, SRBaritone srb, TaskRunner tasks) {
        if (controller == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        int x = 4;
        int y = 4;
        int w = 210;
        int h = 54;

        ctx.fill(x, y, x + w, y + h, 0xAA101018);
        ctx.fill(x, y, x + 3, y + h, 0xFF55CCFF);

        ctx.drawTextWithShadow(mc.textRenderer,
                "§bSR§fBot §8v" + SpeedrunBotMod.VERSION + " §8· §71.21.11",
                x + 8, y + 5, 0xFFFFFF);

        String phase = controller.isRunning()
                ? "§a● §e" + controller.getPhase().label
                : "§8○ §7" + controller.getPhase().label;
        ctx.drawTextWithShadow(mc.textRenderer, phase, x + 8, y + 17, 0xFFFFFF);

        String srbLine = "§7path ";
        if (srb != null) {
            srbLine += srb.isPathing() ? "§aON" : "§8off";
            srbLine += " §8· §f" + srb.getStatus();
        }
        ctx.drawTextWithShadow(mc.textRenderer, srbLine, x + 8, y + 29, 0xFFFFFF);

        String task = tasks != null ? tasks.statusLine() : "idle";
        if (task.length() > 32) task = task.substring(0, 32) + "…";
        ctx.drawTextWithShadow(mc.textRenderer, "§b› §f" + task, x + 8, y + 41, 0xFFFFFF);
    }
}
