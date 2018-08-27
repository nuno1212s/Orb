package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.events.ClanPlayerLeaveEvent;
import com.nuno1212s.events.EventMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class PlayerLeaveClanListener implements Listener {

    @EventHandler
    public void onLeave(ClanPlayerLeaveEvent e) {
        if (EventMain.getIns().getWarEvent().isClanRegistered(e.getClan().getClanID())) {

            List<UUID> playersRegistered = EventMain.getIns().getWarEvent().getPlayersRegistered(e.getClan().getClanID());

            if (playersRegistered.contains(e.getPlayerID())) {
                EventMain.getIns().getWarEvent().removePlayer(e.getPlayerID());
            }
        }
    }

}
