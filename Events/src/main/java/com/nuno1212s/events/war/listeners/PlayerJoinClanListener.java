package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.events.ClanPlayerJoinEvent;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.WarEventScheduler;
import com.nuno1212s.main.MainData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinClanListener implements Listener {

    @EventHandler
    public void onClan(ClanPlayerJoinEvent e) {
        if (EventMain.getIns().getWarEvent().isClanRegistered(e.getClan().getClanID())) {

            if (EventMain.getIns().getWarEvent().getPlayersRegistered(e.getClan().getClanID()).size() < WarEventScheduler.MAX_START_PLAYERS) {

                Player p = Bukkit.getServer().getPlayer(e.getPlayerID());

                if (p == null) {
                    return;
                }

                MainData.getIns().getMessageManager().getMessage("CLAN_REGISTERED_WAR_EVENT")
                        .sendTo(p);

            }

        }
    }

}
