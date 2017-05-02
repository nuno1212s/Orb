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

    public ChatManager() {
        this.chatActivated = true;
        this.separator = " » ";
    }

}
