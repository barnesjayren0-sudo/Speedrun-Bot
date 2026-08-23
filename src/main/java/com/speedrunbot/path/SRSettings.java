package com.speedrunbot.path;

public final class SRSettings {

    public static int maxPathNodes = 40000;
    public static int mineSearchRange = 40;
    public static double arriveDist = 0.7;
    public static float lookSpeed = 0.6f;
    public static boolean sprint = true;
    public static boolean allowSprint = true;
    public static int stuckMs = 1800;
    public static int repathMs = 3000;
    public static long taskTimeoutMs = 180_000;

    // Background chains
    public static boolean foodChain = true;
    public static boolean unstuckChain = true;
    public static boolean mobDefense = true;
    public static int mobRange = 5;
    public static int foodThreshold = 14;

    public static boolean chatSpam = false;

    private SRSettings() {}
}
