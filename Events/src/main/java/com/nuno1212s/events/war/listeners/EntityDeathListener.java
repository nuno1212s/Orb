package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getEntity().getUniqueId());

        if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

            if (EventMain.getIns().getWarEvent().getOnGoing() != null) {

                if (EventMain.getIns().getWarEvent().getOnGoing().isPlayerParticipating(playerData.getPlayerID())) {

                    EventMain.getIns().getWarEvent().getOnGoing().kill(e.getEntity().getKiller(), e.getEntity());

                }

            }

        }

    }

}
