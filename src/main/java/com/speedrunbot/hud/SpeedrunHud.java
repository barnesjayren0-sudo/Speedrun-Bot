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
        int x = 4, y = 4;
        ctx.fill(x, y, x + 200, y + 52, 0x99000000);
        ctx.drawTextWithShadow(mc.textRenderer, "§6SR §fBot §8v" + SpeedrunBotMod.VERSION, x + 4, y + 4, 0xFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, "§7Phase: §e" + controller.getPhase().label, x + 4, y + 16, 0xFFFFFF);
        String path = srb != null && srb.isPathing() ? "§apath" : "§8—";
        ctx.drawTextWithShadow(mc.textRenderer,
                path + " §8| §7" + (srb != null ? srb.getStatus() : ""),
                x + 4, y + 28, 0xFFFFFF);
        String task = tasks != null ? tasks.statusLine() : "";
        if (task.length() > 28) task = task.substring(0, 28) + "…";
        ctx.drawTextWithShadow(mc.textRenderer, "§bTask: §f" + task, x + 4, y + 40, 0xFFFFFF);
    }
}
