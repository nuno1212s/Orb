package com.nuno1212s.npcinbox.chat;

import com.nuno1212s.rewards.Reward;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Manages chat
 */
public class ChatManager {

    @Getter
    private Map<UUID, MessageBuilder> messageBuilder;

    public ChatManager() {
        //We can use weak hashmap to prevent some sort of memory even though we handle the player disconnect, there might still be some problems
        messageBuilder = new WeakHashMap<>();
    }

    public void registerPlayer(UUID player, Reward unfinishedReward) {
        messageBuilder.put(player, new MessageBuilder(unfinishedReward));
    }

    public void unregisterPlayer(UUID player) {
        if (messageBuilder.containsKey(player))
            messageBuilder.remove(player);
    }

    public MessageBuilder getPlayerMessageBuilder(UUID player) {
        return messageBuilder.getOrDefault(player, null);
    }


}
