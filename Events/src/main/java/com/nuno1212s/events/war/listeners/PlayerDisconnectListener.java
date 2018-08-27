package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        List<UUID> playersRegistered = EventMain.getIns().getWarEvent().getPlayersRegistered();

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {
            EventMain.getIns().getWarEvent().getSelectPlayersInventory()
                    .updateInventoriesFor(ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan()));
        }

        if (playersRegistered.contains(e.getPlayer().getUniqueId())) {
            EventMain.getIns().getWarEvent().removePlayer(e.getPlayer().getUniqueId());
        }

    }

}
