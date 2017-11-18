package com.nuno1212s.events;

import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRewardUpdateEvent extends Event {

    @Getter
    @Setter
    private PlayerData playerInfo;

    public PlayerRewardUpdateEvent(PlayerData coreData) {
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
