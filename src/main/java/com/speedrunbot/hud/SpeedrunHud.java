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
        int x = 4, y = 4, w = 220, h = 56;

        ctx.fill(x, y, x + w, y + h, 0xB00C0C14);
        ctx.fill(x, y, x + 3, y + h, 0xFF3DDCFF);

        ctx.drawTextWithShadow(mc.textRenderer,
                "§bSR§fBot §8" + SpeedrunBotMod.VERSION + " §8· §71.21.11",
                x + 8, y + 5, 0xFFFFFF);

        String phase = controller.isRunning()
                ? "§a● §e" + controller.getPhase().label
                : "§8○ §7idle";
        ctx.drawTextWithShadow(mc.textRenderer, phase, x + 8, y + 18, 0xFFFFFF);

        String path = (srb != null && srb.isPathing()) ? "§aPATH" : "§8path";
        String st = srb != null ? srb.getStatus() : "";
        if (st.length() > 18) st = st.substring(0, 18);
        ctx.drawTextWithShadow(mc.textRenderer, path + " §8· §f" + st, x + 8, y + 30, 0xFFFFFF);

        String task = tasks != null ? tasks.statusLine() : "idle";
        if (task.length() > 34) task = task.substring(0, 34) + "…";
        ctx.drawTextWithShadow(mc.textRenderer, "§b› §f" + task, x + 8, y + 42, 0xFFFFFF);
    }
}
