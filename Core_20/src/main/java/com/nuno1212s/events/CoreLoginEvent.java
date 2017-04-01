package com.nuno1212s.events;

import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * The core plugin's login event
 */
public class CoreLoginEvent extends Event {

    @Getter
    private PlayerData playerInfo;

    @Getter
    private AsyncPlayerPreLoginEvent preLoginEvent;

    public CoreLoginEvent(AsyncPlayerPreLoginEvent loginEvent, PlayerData coreData) {
        this.preLoginEvent = loginEvent;
        this.playerInfo = coreData;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
