package com.nuno1212s.displays.player;

/**
 * Handles chat data
 */
public interface ChatData {

    long lastGlobalChatUsage();

    long lastLocalChatUsage();

    void setLastGlobalChatUsage(long time);

    void setLastLocalChatUsage(long time);

    boolean shouldReceive();

}
