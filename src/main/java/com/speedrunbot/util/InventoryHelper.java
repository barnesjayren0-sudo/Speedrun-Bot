package com.speedrunbot.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public final class InventoryHelper {

    private InventoryHelper() {}

    public static int count(MinecraftClient mc, Item item) {
        if (mc.player == null || item == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isOf(item)) n += s.getCount();
        }
        return n;
    }

    public static int countLogs(MinecraftClient mc) {
        if (mc.player == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isIn(ItemTags.LOGS)) n += s.getCount();
        }
        return n;
    }

    /** Iron progress: raw + ingots + ore blocks as items */
    public static int countIronProgress(MinecraftClient mc) {
        return count(mc, Items.RAW_IRON)
                + count(mc, Items.IRON_INGOT)
                + count(mc, Items.IRON_ORE)
                + count(mc, Items.DEEPSLATE_IRON_ORE);
    }

    public static int countDiamonds(MinecraftClient mc) {
        return count(mc, Items.DIAMOND);
    }

    public static Item item(String name) {
        if (name == null) return null;
        String id = name.contains(":") ? name : "minecraft:" + name;
        Identifier ident = Identifier.tryParse(id);
        if (ident != null && Registries.ITEM.containsId(ident)) {
            return Registries.ITEM.get(ident);
        }
        return switch (name.toLowerCase()) {
            case "wood", "log", "logs" -> Items.OAK_LOG;
            case "iron" -> Items.IRON_INGOT;
            case "diamonds" -> Items.DIAMOND;
            case "cobble", "cobblestone" -> Items.COBBLESTONE;
            default -> null;
        };
    }

    public static boolean selectHotbarItem(MinecraftClient mc, Item item) {
        if (mc.player == null || item == null) return false;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                try {
                    mc.player.getInventory().setSelectedSlot(i);
                    return true;
                } catch (Throwable t) {
                    return false;
                }
            }
        }
        return false;
    }
}
