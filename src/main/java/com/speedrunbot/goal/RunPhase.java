package com.speedrunbot.goal;

/** Ordered Any% style phases. Bot advances when each phase reports complete. */
public enum RunPhase {
    IDLE("Idle"),
    WOOD("Wood / tools"),
    STONE("Stone tools"),
    IRON("Iron + buckets"),
    DIAMOND("Diamonds"),
    NETHER("Enter Nether"),
    FORTRESS("Find fortress"),
    BLAZE("Blaze rods"),
    PEARLS("Ender pearls"),
    STRONGHOLD("Stronghold"),
    END("The End"),
    DRAGON("Kill dragon"),
    DONE("Finished");

    public final String label;

    RunPhase(String label) {
        this.label = label;
    }

    public RunPhase next() {
        int i = ordinal() + 1;
        if (i >= values().length) return DONE;
        return values()[i];
    }
}
