package com.speedrunbot.path;

/** Tunables for SRBaritone on 1.21.11 */
public final class SRSettings {

    public static int maxPathNodes = 35000;
    public static int mineSearchRange = 32;
    public static double arriveDist = 0.75;
    public static float lookSpeed = 0.55f;
    public static boolean sprint = true;
    public static boolean allowSprint = true;
    public static int stuckMs = 2000;
    public static int repathMs = 3500;

    private SRSettings() {}
}
