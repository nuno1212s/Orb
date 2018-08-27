package com.nuno1212s.clans.events;

import com.nuno1212s.clans.clanmanager.Clan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClanPlayerKickEvent extends Event {

    private Clan clan;

    private UUID playerID;

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
