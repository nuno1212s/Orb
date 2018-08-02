package com.nuno1212s.rediscommunication;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Message
 */
public class Message {

    @Getter
    private String channel, reason;

    @Getter
    private String OGServer;

    @Getter
    private JSONObject data;

    /**
     * Reads the message from the data
     * @param data
     */
    public Message(byte[] data, String OGServer) {
        this.OGServer = OGServer;
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(data);
        this.channel = dataInput.readUTF();
        this.reason = dataInput.readUTF();

        try (StringReader r = new StringReader(dataInput.readUTF())) {
            this.data = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public Message(String channel, String reason, JSONObject data) {
        this.channel = channel;
        this.reason = reason;
        this.data = data;
        this.OGServer = MainData.getIns().getServerManager().getServerName();
    }

    public Message(String channel) {
        this.channel = channel;
        this.data = new JSONObject();
    }

    public Message setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Message add(String key, JSONObject value) {
        this.data.put(key, value);

        return this;
    }

    public Message add(String key, Object value) {
        this.data.put(key, value);

        return this;
    }

    public byte[] toByteArray() {
        if (reason == null) {
            throw new IllegalArgumentException("Reason needs to be given");
        }

        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF(channel);
        dataOutput.writeUTF(reason);
        dataOutput.writeUTF(data.toJSONString());

        return dataOutput.toByteArray();
    }


}
