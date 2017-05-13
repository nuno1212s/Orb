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
    private long chatTimerGlobal, chatTimerLocal, range;

    public ChatManager() {
        this.chatActivated = true;
        this.separator = " » ";
        this.chatTimerGlobal = 5000;
        this.chatTimerLocal = 2000;
        this.range = (long) Math.pow(25L, 2);
    }

}
