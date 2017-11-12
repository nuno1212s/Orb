package com.nuno1212s.spawners.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerKillBundledEntityEvent extends Event {


    @Getter
    private EntityDamageByEntityEvent e;

    public PlayerKillBundledEntityEvent(EntityDamageByEntityEvent e) {
        this.e = e;
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
