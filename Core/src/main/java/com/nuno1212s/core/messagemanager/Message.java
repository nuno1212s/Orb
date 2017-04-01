package com.nuno1212s.core.messagemanager;

import com.nuno1212s.core.util.Pair;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the message
 * <p>
 * Message instance
 */
public class Message {

    private final Map<Messages.MessageType, Object> messages;

    private final Map<String, String> formats = new HashMap<>();

    Message(String message) {
        messages = new HashMap<>();
        messages.put(Messages.MessageType.TEXT, message);
    }

    Message(Map<Messages.MessageType, Object> messages) {
        this.messages = messages;
    }

    protected Message(List<String> messages) {
        this.messages = new HashMap<>();
        this.messages.put(Messages.MessageType.TEXT, messages);
    }

    public void sendTo(CommandSender... players) {
        send(players);
    }

    private void send(CommandSender... players) {
        this.messages.forEach(((messageType, o) -> {
            if (messageType == Messages.MessageType.TEXT) {
                if (o instanceof ArrayList) {
                    ((ArrayList<String>) o).forEach(msg -> {
                        String message = formatMessage(msg);
                        for (CommandSender player : players) {
                            player.sendMessage(message);
                        }
                    });
                } else {
                    String message = formatMessage((String) o);
                    for (CommandSender player : players) {
                        player.sendMessage(message);
                    }
                }
            } else if (messageType == Messages.MessageType.TITLE) {
                if (!(o instanceof JSONArray)) {
                    return;
                }
                ArrayList<String> messages = (ArrayList<String>) o;
                if (messages.size() < 2) {
                    return;
                }
                String message1 = formatMessage(messages.get(0)), message2 = formatMessage(messages.get(1));
                for (CommandSender player : players) {
                    if (!(player instanceof Player)) {
                        continue;
                    }
                    ((Player) player).sendTitle(message1, message2);
                }
            } else if (messageType == Messages.MessageType.ACTION_BAR) {
                String message = formatMessage((String) o);
                for (CommandSender player : players) {
                    if (!(player instanceof Player)) {
                        continue;
                    }
                    //ActionBarAPI.sendActionBar((Player) player, message);
                }
            } else if (messageType == Messages.MessageType.SOUND) {
                List s = (List) this.messages.get(messageType);
                for (Object soundObj : s) {
                    for (CommandSender sender : players)
                        ((Sound) soundObj).sendToPlayer((Player) sender);
                }
            }
        }));
    }

    private String formatMessage(String message) {
        for (Map.Entry<String, String> formats : this.formats.entrySet()) {
            message = message.replace(formats.getKey(), formats.getValue());
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
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

    Message newInstance() {
        return new Message(this.messages);
    }

}

@AllArgsConstructor
class Sound {

    private String soundName;

    private float pitch, volume;

    void sendToPlayer(Player p) {
        p.playSound(p.getLocation(), soundName, pitch, volume);
    }


}