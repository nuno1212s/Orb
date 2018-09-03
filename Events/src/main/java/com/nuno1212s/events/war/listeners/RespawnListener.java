package com.nuno1212s.events.war.listeners;

import com.nuno1212s.events.EventMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (EventMain.getIns().getWarEvent().getOnGoing() != null) {

            if (EventMain.getIns().getWarEvent().getOnGoing().getAllPlayers().contains(e.getPlayer().getUniqueId())) {

                //Player has died

                e.setRespawnLocation(EventMain.getIns().getWarEvent().getHelper().getFallbackLocation());

            }

        }
    }

}
