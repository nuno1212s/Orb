package com.nuno1212s.rankup.events;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player join events
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onLogin(CoreLoginEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        RUPlayerData data = new RUPlayerData(playerInfo);
        Main.getIns().getMysql().loadPlayerData(data);
        e.setPlayerInfo(data);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        com.nuno1212s.displays.Main.getIns().getScoreboardManager().handlePlayerJoin(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()), e.getPlayer());

        //MainData.getIns().getMessageManager().getMessage("ON_JOIN").sendTo(e.getPlayer());

    }

}
