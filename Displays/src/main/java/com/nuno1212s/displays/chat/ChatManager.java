package com.nuno1212s.displays.chat;

import lombok.Getter;
import lombok.Setter;

/**
 * Chat manager
 */
public class ChatManager {

    @Getter
    @Setter
    private boolean chatActivated;

    @Getter
    private String separator;

    @Getter
    private long chatTimerGlobal, chatTimerLocal, range;

    public ChatManager() {
        this.chatActivated = true;
        this.separator = " » ";
        this.chatTimerGlobal = 15000;
        this.chatTimerLocal = 3;
        this.range = (long) Math.pow(25L, 2);
    }

}
