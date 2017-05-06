package com.nuno1212s.displays.chat;

import lombok.Getter;

/**
 * Chat manager
 */
public class ChatManager {

    @Getter
    private boolean chatActivated;

    @Getter
    private String separator;

    @Getter
    private long chatTimer, range;

    public ChatManager() {
        this.chatActivated = true;
        this.separator = " » ";
        this.chatTimer = 5000;
        this.range = (long) Math.pow(25L, 2);
    }

}
