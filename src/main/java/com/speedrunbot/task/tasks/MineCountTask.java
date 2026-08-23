package com.speedrunbot.task.tasks;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import com.speedrunbot.util.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MineCountTask extends Task {

    private final String blockId;
    private final int count;
    private Item dropItem;
    private boolean mining;

    public MineCountTask(String blockId, int count) {
        this.blockId = blockId;
        this.count = Math.max(1, count);
    }

    @Override
    protected void onStart(MinecraftClient mc, SRBaritone srb) {
        resolveDrop();
        if (srb != null) {
            srb.mine(mc, blockId);
            mining = true;
        }
    }

    private void resolveDrop() {
        String full = blockId.contains(":") ? blockId : "minecraft:" + blockId;
        Identifier id = Identifier.tryParse(full);
        if (id != null && Registries.BLOCK.containsId(id)) {
            Block b = Registries.BLOCK.get(id);
            dropItem = b.asItem();
        }
        // ore → raw / item approximations
        if (blockId.contains("iron_ore")) dropItem = Items.RAW_IRON;
        if (blockId.contains("diamond_ore")) dropItem = Items.DIAMOND;
        if (blockId.contains("coal_ore")) dropItem = Items.COAL;
        if (blockId.contains("gold_ore")) dropItem = Items.RAW_GOLD;
        if (blockId.equals("stone")) dropItem = Items.COBBLESTONE;
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (!mining && srb != null) {
            srb.mine(mc, blockId);
            mining = true;
        }
        // restart mine if SRBaritone went idle early
        if (srb != null && !srb.isRunning() && !isFinished(mc, srb)) {
            srb.mine(mc, blockId);
        }
        return null;
    }

    @Override
    public boolean isFinished(MinecraftClient mc, SRBaritone srb) {
        if (blockId.contains("log") || blockId.equals("oak_log")) {
            return InventoryHelper.countLogs(mc) >= count;
        }
        if (blockId.contains("iron_ore")) {
            return InventoryHelper.countIronProgress(mc) >= count;
        }
        if (blockId.contains("diamond_ore")) {
            return InventoryHelper.countDiamonds(mc) >= count;
        }
        if (dropItem != null) {
            return InventoryHelper.count(mc, dropItem) >= count;
        }
        return false;
    }

    @Override
    protected void onStop(MinecraftClient mc, SRBaritone srb) {
        if (srb != null) srb.stop(mc);
        super.onStop(mc, srb);
    }

    @Override
    public String getName() {
        return "Mine(" + blockId + " x" + count + ")";
    }
}
