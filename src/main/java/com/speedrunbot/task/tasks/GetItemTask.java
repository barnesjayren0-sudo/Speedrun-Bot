package com.speedrunbot.task.tasks;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import com.speedrunbot.util.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

/** High-level #get — catalogue maps items → mine tasks. */
public class GetItemTask extends Task {

    private final String itemName;
    private final int count;
    private boolean childSpawned;

    public GetItemTask(String itemName, int count) {
        this.itemName = itemName.toLowerCase().replace("minecraft:", "");
        this.count = Math.max(1, count);
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (isFinished(mc, srb)) return null;
        if (childSpawned) return null; // subtask already running / finished cycle
        childSpawned = true;
        return resolve();
    }

    private Task resolve() {
        return switch (itemName) {
            case "oak_log", "log", "logs", "wood" -> new MineCountTask("oak_log", count);
            case "cobblestone", "cobble", "stone" -> new MineCountTask("stone", count);
            case "coal" -> new MineCountTask("coal_ore", count);
            case "raw_iron", "iron_ore" -> new MineCountTask("iron_ore", count);
            case "iron_ingot", "iron" -> new MineCountTask("iron_ore", count);
            case "diamond", "diamonds" -> new MineCountTask("diamond_ore", count);
            case "gold_ingot", "raw_gold", "gold" -> new MineCountTask("gold_ore", count);
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
        return switch (itemName) {
            case "oak_log", "log", "logs", "wood" -> InventoryHelper.countLogs(mc);
            case "iron", "iron_ingot", "raw_iron", "iron_ore" -> InventoryHelper.countIronProgress(mc);
            case "diamond", "diamonds" -> InventoryHelper.countDiamonds(mc);
            case "cobblestone", "cobble" -> InventoryHelper.count(mc, Items.COBBLESTONE);
            case "coal" -> InventoryHelper.count(mc, Items.COAL) + InventoryHelper.count(mc, Items.CHARCOAL);
            default -> {
                var item = InventoryHelper.item(itemName);
                yield item != null ? InventoryHelper.count(mc, item) : 0;
            }
        };
    }

    @Override
    public String getName() {
        return "Get(" + itemName + " x" + count + ")";
    }
}
