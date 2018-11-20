package com.nuno1212s.duels.listeners;

import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.duels.duelmanager.Duel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Duel d = DuelMain.getIns().getDuelManager().getActiveDuelForPlayer(e.getPlayer().getUniqueId());

        if (d != null && d.isSpectator(e.getPlayer().getUniqueId())) {

            e.setCancelled(true);

        }

    }

}
