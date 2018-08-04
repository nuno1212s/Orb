package com.nuno1212s.tradewindow.chathandlers;

import com.nuno1212s.main.MainData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatRequests implements Listener {

    private Map<UUID, CompletableFuture<String>> requests;

    public ChatRequests() {
        this.requests = new HashMap<>();
    }

    public CompletableFuture<String> requestChatInformation(Player player, String message) {
        CompletableFuture<String> response = new CompletableFuture<>();

        this.requests.put(player.getUniqueId(), response);

        MainData.getIns().getMessageManager().getMessage(message).sendTo(player);

        return response;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (requests.containsKey(e.getPlayer().getUniqueId())) {

            CompletableFuture<String> future = requests.get(e.getPlayer().getUniqueId());

            requests.remove(e.getPlayer().getUniqueId());

            future.complete(e.getMessage());

            e.setCancelled(true);
        }
    }

}
