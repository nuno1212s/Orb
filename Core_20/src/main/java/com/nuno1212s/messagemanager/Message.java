package com.nuno1212s.messagemanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.messagetypes.IMessage;
import com.nuno1212s.messagemanager.messagetypes.StringMessage;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Message classes
 */
public class Message {

    private final List<IMessage> messages;

    private final Map<String, String> formats;

    public Message() {
        this.messages = new ArrayList<>();
        this.formats = new HashMap<>();
        this.messages.add(new StringMessage(""));
    }

    public Message(List<IMessage> messages) {
        this.messages = messages;
        this.formats = new HashMap<>();
    }

    @SafeVarargs
    public final Message format(Pair<String, String>... strings) {
        if (strings.length != 0) {
            for (Pair<String, String> string : strings) {
                this.formats.put(string.getKey(), string.getValue());
            }
        }
        return this;
    }

    public final Message format(List<Pair<String, String>> strings) {
        if (strings.size() != 0) {
            for (Pair<String, String> string : strings) {
                this.formats.put(string.getKey(), string.getValue());
            }
        }
        return this;
    }

    public final Message format(String s1, String s2) {
        this.formats.put(s1, s2);
        return this;
    }

    public final Message format(String s1, Object s2) {
        this.formats.put(s1, s2.toString());
        return this;
    }

    public void sendTo(CommandSender... players) {
        if (MainData.getIns().isBungee()) {
            return;
        }
        if (!Bukkit.isPrimaryThread()) {
            MainData.getIns().getScheduler().runTask(() ->
                sendTo(players)
            );
            return;
        }
        send(players);
    }

    public void sendTo(Collection<? extends CommandSender> players) {
        if (MainData.getIns().isBungee()) {
            return;
        }

        send(players.toArray(new CommandSender[players.size()]));
    }

    public void sendTo(PlayerData d) {
        if (MainData.getIns().isBungee()) {
            return;
        }

        if(!Bukkit.isPrimaryThread()) {
            MainData.getIns().getScheduler().runTask(() -> {
                Player p = d.getPlayerReference(Player.class);

                if (p == null) {
                    return;
                }

                send(p);
            });
        } else {
            Player p = d.getPlayerReference(Player.class);
            if (p == null) {
                return;
            }
            send(p);
        }
    }

    private void send(CommandSender... sender) {
        this.messages.forEach(message -> message.sendTo(this.formats, sender));
    }

    public Message newInstance() {
        return new Message(this.messages);
    }

    @Override
    public String toString() {
        for (IMessage message : this.messages) {
            if (message instanceof StringMessage) {
                return ((StringMessage) message).toString(this.formats);
            }
        }
        return "";
    }
}

