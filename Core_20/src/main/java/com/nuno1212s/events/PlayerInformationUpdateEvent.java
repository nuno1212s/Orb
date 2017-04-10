package com.nuno1212s.events;

import com.nuno1212s.playermanager.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when player information is updated
 *
 * THis event should trigger scoreboard and other similar displays
 */
@AllArgsConstructor
public class PlayerInformationUpdateEvent extends Event {

    @Getter
    private final PlayerData player;

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
