package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.WarEventScheduler;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        //The war event is starting soon
        if (EventMain.getIns().getWarEvent().canRegisterClan()) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

            if (playerData instanceof ClanPlayer) {

                Clan clan = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

                EventMain.getIns().getWarEvent().getSelectPlayersInventory().updateInventoriesFor(clan);

                if (EventMain.getIns().getWarEvent().isClanRegistered(clan.getClanID())) {
                    List<UUID> playersRegistered = EventMain.getIns().getWarEvent().getPlayersRegistered(clan.getClanID());

                    if (playersRegistered.size() < WarEventScheduler.MAX_PLAYERS_PER_CLAN) {

                        MainData.getIns().getMessageManager().getMessage("SPACE_AVAILABLE_ON_WAR_EVENT").sendTo(e.getPlayer());

                    } else {
                        MainData.getIns().getMessageManager().getMessage("CLAN_SIGNED_UP_BUT_NO_SPACE").sendTo(e.getPlayer());
                    }

                }
            }
        }

    }

}
