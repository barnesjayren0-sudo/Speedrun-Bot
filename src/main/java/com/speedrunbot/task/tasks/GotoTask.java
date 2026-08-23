package com.speedrunbot.task.tasks;

import com.speedrunbot.path.GoalBlock;
import com.speedrunbot.path.GoalXZ;
import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class GotoTask extends Task {

    private final Integer x, y, z;
    private boolean issued;

    public GotoTask(int x, int y, int z) {
        this.x = x; this.y = y; this.z = z;
    }

    public GotoTask(int x, int z) {
        this.x = x; this.y = null; this.z = z;
    }

    @Override
    protected void onStart(MinecraftClient mc, SRBaritone srb) {
        if (y != null) srb.gotoPos(mc, x, y, z);
        else srb.gotoXZ(mc, x, z);
        issued = true;
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (!issued) onStart(mc, srb);
        if (!srb.isPathing() && !srb.isRunning()) {
            // repath if not arrived
            if (!isFinished(mc, srb)) {
                if (y != null) srb.gotoPos(mc, x, y, z);
                else srb.gotoXZ(mc, x, z);
            }
        }
        return null;
    }

    @Override
    public boolean isFinished(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null) return true;
        BlockPos p = mc.player.getBlockPos();
        if (y != null) return Math.abs(p.getX() - x) <= 1 && Math.abs(p.getZ() - z) <= 1
                && Math.abs(p.getY() - y) <= 2;
        return Math.abs(p.getX() - x) <= 2 && Math.abs(p.getZ() - z) <= 2;
    }

    @Override
    public String getName() {
        return y != null ? "Goto(" + x + "," + y + "," + z + ")" : "GotoXZ(" + x + "," + z + ")";
    }
}
