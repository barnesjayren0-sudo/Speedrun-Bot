package com.speedrunbot.goal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

public final class InventoryGoals {

    private InventoryGoals() {}

    public static int count(MinecraftClient mc, Item item) {
        if (mc.player == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isOf(item)) n += s.getCount();
        }
        return n;
    }

    public static boolean hasLogs(MinecraftClient mc, int min) {
        if (mc.player == null) return false;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isIn(ItemTags.LOGS)) n += s.getCount();
        }
        return n >= min;
    }

    public static boolean hasPickaxeAtLeast(MinecraftClient mc, String material) {
        if (mc.player == null) return false;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isIn(ItemTags.PICKAXES)) continue;
            String id = s.getItem().toString().toLowerCase();
            if (material.equals("wood") && (id.contains("wood") || id.contains("wooden"))) return true;
            if (material.equals("stone") && id.contains("stone")) return true;
            if (material.equals("iron") && id.contains("iron")) return true;
            if (material.equals("diamond") && id.contains("diamond")) return true;
            if (material.equals("netherite") && id.contains("netherite")) return true;
        }
        return false;
    }

    public static boolean hasIronToolsBasics(MinecraftClient mc) {
        return count(mc, Items.IRON_INGOT) >= 3 || hasPickaxeAtLeast(mc, "iron");
    }

    public static boolean hasDiamonds(MinecraftClient mc, int n) {
        return count(mc, Items.DIAMOND) >= n || hasPickaxeAtLeast(mc, "diamond");
    }

    public static boolean hasBlazeRods(MinecraftClient mc, int n) {
        return count(mc, Items.BLAZE_ROD) >= n || count(mc, Items.BLAZE_POWDER) >= n * 2;
    }

    public static boolean hasPearls(MinecraftClient mc, int n) {
        return count(mc, Items.ENDER_PEARL) >= n;
    }

    public static boolean hasEyes(MinecraftClient mc, int n) {
        return count(mc, Items.ENDER_EYE) >= n;
    }

    public static boolean inNether(MinecraftClient mc) {
        return mc.world != null && mc.world.getRegistryKey().getValue().getPath().contains("nether");
    }

    public static boolean inEnd(MinecraftClient mc) {
        return mc.world != null && mc.world.getRegistryKey().getValue().getPath().contains("end");
    }
}
