package com.nuno1212s.events.war.listeners;

import com.nuno1212s.clans.events.ClanDeleteEvent;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.WarEvent;
import com.nuno1212s.main.MainData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class ClanDisbandListener implements Listener {

    @EventHandler
    public void onDisband(ClanDeleteEvent e) {
        if (EventMain.getIns().getWarEvent().isClanRegistered(e.getClan().getClanID())) {
            EventMain.getIns().getWarEvent().disqualify(e.getClan());
        } else {
            WarEvent onGoing = EventMain.getIns().getWarEvent().getOnGoing();
            if (onGoing != null) {

                if (onGoing.isParticipating(e.getClan().getClanID())) {
                    List<UUID> alivePlayersForClan = onGoing.getAlivePlayersForClan(e.getClan().getClanID());

                    EventMain.getIns().getWarEvent().getHelper().sendMessage(alivePlayersForClan,
                            MainData.getIns().getMessageManager().getMessage("CLAN_HAS_BEEN_DISBANDED"));

                    alivePlayersForClan.forEach((player) -> onGoing.kill(null, Bukkit.getPlayer(player)));
                }

            }
        }
    }

}
