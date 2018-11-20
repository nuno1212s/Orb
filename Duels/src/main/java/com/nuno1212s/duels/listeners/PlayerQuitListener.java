package com.nuno1212s.duels.listeners;

import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.duels.duelmanager.Duel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Duel d = DuelMain.getIns().getDuelManager().getActiveDuelForPlayer(e.getPlayer().getUniqueId());

        if (d != null && d.isSpectator(e.getPlayer().getUniqueId())) {

            d.removeAsSpectator(e.getPlayer());

        } else if (d != null) {

            // TODO: 20-11-2018 Kill the player and remove him from the arena.

        }

    }

}
