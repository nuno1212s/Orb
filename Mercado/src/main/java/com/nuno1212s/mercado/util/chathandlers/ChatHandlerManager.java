package com.nuno1212s.mercado.util.chathandlers;

import com.nuno1212s.util.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles chat stuff
 */
public class ChatHandlerManager {

    private Map<UUID, Callback<?>> players;

    public ChatHandlerManager() {
        players = new HashMap<>();
    }

    public void addCallback(UUID player, Callback<?> c) {
        this.players.put(player, c);
    }

    public boolean hasCallback(UUID player) {
        return this.players.containsKey(player);
    }

    public Callback<?> getCallback(UUID player) {
        return players.get(player);
    }

    public void removeCallback(UUID player) {
        this.players.remove(player);
    }

}
