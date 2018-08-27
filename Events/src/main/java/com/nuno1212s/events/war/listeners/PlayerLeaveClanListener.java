package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.events.ClanPlayerLeaveEvent;
import com.nuno1212s.events.EventMain;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class PlayerLeaveClanListener implements Listener {

    @EventHandler
    public void onLeave(ClanPlayerLeaveEvent e) {
        if (EventMain.getIns().getWarEvent().canRegisterClan()) {

            //Only update the inventories if the registering is active
            EventMain.getIns().getWarEvent().getSelectPlayersInventory()
                    .updateInventoriesFor(e.getClan());
        }


        if (EventMain.getIns().getWarEvent().isClanRegistered(e.getClan().getClanID())) {

            List<UUID> playersRegistered = EventMain.getIns().getWarEvent().getPlayersRegistered(e.getClan().getClanID());

            if (playersRegistered.contains(e.getPlayerID())) {
                EventMain.getIns().getWarEvent().removePlayer(e.getPlayerID());
            }
        }

        if (EventMain.getIns().getWarEvent().getOnGoing() != null) {
            EventMain.getIns().getWarEvent().getOnGoing().kill(null, Bukkit.getPlayer(e.getPlayerID()));
        }
    }

}
