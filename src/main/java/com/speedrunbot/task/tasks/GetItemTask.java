package com.speedrunbot.task.tasks;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * High-level #get — maps common items to mine targets (AltoClef catalogue lite).
 */
public class GetItemTask extends Task {

    private final String itemName;
    private final int count;
    private Task child;

    public GetItemTask(String itemName, int count) {
        this.itemName = itemName.toLowerCase().replace("minecraft:", "");
        this.count = Math.max(1, count);
    }

    @Override
    protected void onStart(MinecraftClient mc, SRBaritone srb) {
        child = resolve();
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (isFinished(mc, srb)) return null;
        if (child == null) child = resolve();
        return child;
    }

    private Task resolve() {
        return switch (itemName) {
            case "oak_log", "log", "logs", "wood" -> new MineCountTask("oak_log", count);
            case "cobblestone", "stone" -> new MineCountTask("stone", count);
            case "coal" -> new MineCountTask("coal_ore", count);
            case "raw_iron", "iron_ore" -> new MineCountTask("iron_ore", count);
            case "iron_ingot", "iron" -> new MineCountTask("iron_ore", Math.max(count, count)); // smelt later
            case "diamond", "diamonds" -> new MineCountTask("diamond_ore", count);
            case "gold_ingot", "raw_gold" -> new MineCountTask("gold_ore", count);
            case "dirt" -> new MineCountTask("dirt", count);
            case "sand" -> new MineCountTask("sand", count);
            default -> new MineCountTask(itemName, count);
        };
    }

    @Override
    public boolean isFinished(MinecraftClient mc, SRBaritone srb) {
        return countOwned(mc) >= count;
    }

    private int countOwned(MinecraftClient mc) {
        if (mc.player == null) return 0;
        Item target = resolveItem();
        if (target == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isOf(target)) n += s.getCount();
            // accept raw + ingot for iron
            if ((itemName.contains("iron")) && (s.isOf(Items.RAW_IRON) || s.isOf(Items.IRON_INGOT) || s.isOf(Items.IRON_ORE))) {
                n += s.getCount();
            }
            if (itemName.contains("diamond") && s.isOf(Items.DIAMOND)) n += s.getCount();
        }
        return n;
    }

    private Item resolveItem() {
        Identifier id = Identifier.tryParse("minecraft:" + itemName);
        if (id != null && Registries.ITEM.containsId(id)) return Registries.ITEM.get(id);
        return switch (itemName) {
            case "wood", "log", "logs" -> Items.OAK_LOG;
            case "iron" -> Items.IRON_INGOT;
            case "diamonds" -> Items.DIAMOND;
            default -> null;
        };
    }

    @Override
    public String getName() {
        return "Get(" + itemName + " x" + count + ")";
    }
}
