package com.nuno1212s.classes.player;

/**
 * Kit interface;
 */
public interface KitPlayer {

    boolean canUseKit(int kitID, long delay);

    long timeUntilUsage(int kitID, long delay);

    long lastUsage(int kitID);

    void registerKitUsage(int kitID, long time);

    void unregisterKitUsage(int kitID);

}
