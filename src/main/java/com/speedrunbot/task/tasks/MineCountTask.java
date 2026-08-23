package com.speedrunbot.task.tasks;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/** Mine until inventory has `count` of the block's item (AltoClef collect-style). */
public class MineCountTask extends Task {

    private final String blockId;
    private final int count;
    private Item item;
    private boolean startedMine;

    public MineCountTask(String blockId, int count) {
        this.blockId = blockId;
        this.count = Math.max(1, count);
    }

    @Override
    protected void onStart(MinecraftClient mc, SRBaritone srb) {
        String full = blockId.contains(":") ? blockId : "minecraft:" + blockId;
        Identifier id = Identifier.tryParse(full);
        if (id != null && Registries.BLOCK.containsId(id)) {
            Block b = Registries.BLOCK.get(id);
            item = b.asItem();
        }
        srb.mine(mc, blockId);
        startedMine = true;
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (!startedMine) onStart(mc, srb);
        if (!srb.isRunning() && !isFinished(mc, srb)) {
            srb.mine(mc, blockId);
        }
        return null;
    }

    @Override
    public boolean isFinished(MinecraftClient mc, SRBaritone srb) {
        return countItems(mc) >= count;
    }

    private int countItems(MinecraftClient mc) {
        if (mc.player == null || item == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isOf(item)) n += s.getCount();
        }
        return n;
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
