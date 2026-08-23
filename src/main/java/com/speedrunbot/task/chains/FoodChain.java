package com.speedrunbot.task.chains;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.path.SRSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class FoodChain {

    private long lastEat;

    public void tick(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getHungerManager().getFoodLevel() > SRSettings.foodThreshold) return;
        if (mc.player.isUsingItem()) return;
        if (System.currentTimeMillis() - lastEat < 1600) return;

        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            try {
                if (s.get(DataComponentTypes.FOOD) == null) continue;
            } catch (Throwable t) {
                continue;
            }
            try {
                mc.player.getInventory().setSelectedSlot(i);
            } catch (Throwable ignored) {}
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            lastEat = System.currentTimeMillis();
            return;
        }
    }
}
