package com.nuno1212s.minas.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Block event for when a player breaks a block in a mine
 */
public class PlayerBreakBlockMineEvent extends Event {

    @Getter
    private BlockBreakEvent e;

    public PlayerBreakBlockMineEvent(BlockBreakEvent e) {
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
