package com.nuno1212s.duels.events;

import com.nuno1212s.duels.arenas.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaClearEvent extends Event {

    public Arena arena;

    public ArenaClearEvent(Arena arena) {
        this.arena = arena;
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
