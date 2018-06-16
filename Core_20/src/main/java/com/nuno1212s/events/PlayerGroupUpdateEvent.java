package com.nuno1212s.events;

import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class PlayerGroupUpdateEvent extends Event {

    @Getter
    private final PlayerData player;

    @Getter
    private final Group previousGroup;

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
