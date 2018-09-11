package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.WarEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Player) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getEntity().getUniqueId());

            if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

                WarEvent onGoing = EventMain.getIns().getWarEvent().getOnGoing();
                if (onGoing != null) {

                    if (!onGoing.canDamage() && onGoing.isPlayerParticipating(e.getEntity().getUniqueId())) {

                        e.setCancelled(true);

                    }

                } else if (EventMain.getIns().getWarEvent().getPlayersRegistered().contains(playerData.getPlayerID())) {

                    e.setCancelled(true);

                }
            }
        }

    }

}
