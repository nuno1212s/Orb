package com.nuno1212s.rankup.events;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.events.PlayerInformationLoadEvent;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles player join events
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onLogin(CoreLoginEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        RUPlayerData data = Main.getIns().getMysql().loadPlayerData(playerInfo);
        e.setPlayerInfo(data);
    }

    @EventHandler
    public void onPlayerInfoLoad(PlayerInformationLoadEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        RUPlayerData data = Main.getIns().getMysql().loadPlayerData(playerInfo);
        e.setPlayerInfo(data);
    }

}
