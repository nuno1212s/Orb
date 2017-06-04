package com.nuno1212s.rediscommunication;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Message
 */
public class Message {

    @Getter
    ArrayList<String> messages;

    @Getter
    String reason;

    @Getter
    byte[] bytes;

    public Message(String fromString) {
        String[] messages = fromString.split("  ");
        ByteArrayDataOutput data = ByteStreams.newDataOutput();

        for (String message : messages) {
            data.writeUTF(message);
        }

        this.bytes = data.toByteArray();

    }

    public String toString() {
        ByteArrayDataOutput array = ByteStreams.newDataOutput();
        array.writeUTF(reason);
        messages.forEach(array::writeUTF);
        return new String(array.toByteArray());
    }

}
