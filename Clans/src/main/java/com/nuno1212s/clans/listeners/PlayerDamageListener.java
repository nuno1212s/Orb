package com.nuno1212s.clans.listeners;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getDamager().getUniqueId()),
                    d2 = MainData.getIns().getPlayerManager().getPlayer(e.getEntity().getUniqueId());

            if (d instanceof ClanPlayer && d2 instanceof ClanPlayer) {

                e.setCancelled(((ClanPlayer) d).getClan().equals(((ClanPlayer) d2).getClan()));

            }

        }

    }

}
