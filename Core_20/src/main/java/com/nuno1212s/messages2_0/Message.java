package com.nuno1212s.messages2_0;

import com.nuno1212s.messages2_0.messagetypes.IMessage;
import com.nuno1212s.messages2_0.messagetypes.StringMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message classes
 */

public class Message {

    private List<IMessage> messages;

    private Map<String, String> formats;

    public Message() {
        this.messages = new ArrayList<>();
        this.formats = new HashMap<>();
        this.messages.add(new StringMessage(""));
    }

    public Message(List<IMessage> messages) {
        this.messages = messages;
        this.formats = new HashMap<>();
    }

    public Message newInstance() {
        return new Message(this.messages);
    }

}

