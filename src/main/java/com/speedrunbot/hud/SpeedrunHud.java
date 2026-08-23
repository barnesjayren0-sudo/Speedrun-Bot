package com.speedrunbot.hud;

import com.speedrunbot.SpeedrunBotMod;
import com.speedrunbot.baritone.BaritoneBridge;
import com.speedrunbot.controller.SpeedrunController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class SpeedrunHud {

    private SpeedrunHud() {}

    public static void render(DrawContext ctx, SpeedrunController controller, BaritoneBridge baritone) {
        if (controller == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();

        int x = 4;
        int y = 4;
        ctx.fill(x, y, x + 160, y + 42, 0x99000000);
        ctx.drawTextWithShadow(mc.textRenderer, "§6SR §fBot §8v" + SpeedrunBotMod.VERSION, x + 4, y + 4, 0xFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer,
                "§7Phase: §e" + controller.getPhase().label,
                x + 4, y + 16, 0xFFFFFF);
        String bati = baritone != null && baritone.isAvailable() ? "§aON" : "§cOFF";
        String run = controller.isRunning() ? "§aRUN" : "§8idle";
        ctx.drawTextWithShadow(mc.textRenderer,
                run + " §8| §7Bati " + bati + " §8| §7" + format(controller.getRunElapsedMs()),
                x + 4, y + 28, 0xFFFFFF);
    }

    private static String format(long ms) {
        long s = ms / 1000;
        long m = s / 60;
        s %= 60;
        return m + ":" + (s < 10 ? "0" : "") + s;
    }
}
