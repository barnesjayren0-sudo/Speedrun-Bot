package com.speedrunbot.task.tasks;

import com.speedrunbot.path.SRBaritone;
import com.speedrunbot.task.Task;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class GotoTask extends Task {

    private final int x;
    private final Integer y;
    private final int z;
    private boolean issued;
    private int repaths;

    public GotoTask(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GotoTask(int x, int z) {
        this.x = x;
        this.y = null;
        this.z = z;
    }

    @Override
    protected void onStart(MinecraftClient mc, SRBaritone srb) {
        issue(mc, srb);
    }

    private void issue(MinecraftClient mc, SRBaritone srb) {
        if (srb == null) return;
        if (y != null) srb.gotoPos(mc, x, y, z);
        else srb.gotoXZ(mc, x, z);
        issued = true;
        repaths++;
    }

    @Override
    protected Task onTick(MinecraftClient mc, SRBaritone srb) {
        if (!issued) issue(mc, srb);
        if (srb != null && !srb.isPathing() && !srb.isRunning() && !isFinished(mc, srb) && repaths < 8) {
            issue(mc, srb);
        }
        return null;
    }

    @Override
    public boolean isFinished(MinecraftClient mc, SRBaritone srb) {
        if (mc.player == null) return true;
        BlockPos p = mc.player.getBlockPos();
        if (y != null) {
            return Math.abs(p.getX() - x) <= 1
                    && Math.abs(p.getZ() - z) <= 1
                    && Math.abs(p.getY() - y) <= 2;
        }
        return Math.abs(p.getX() - x) <= 2 && Math.abs(p.getZ() - z) <= 2;
    }

    @Override
    public String getName() {
        return y != null ? "Goto(" + x + "," + y + "," + z + ")" : "GotoXZ(" + x + "," + z + ")";
    }
}
