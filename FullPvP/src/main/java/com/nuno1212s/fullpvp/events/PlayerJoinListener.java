package com.nuno1212s.fullpvp.events;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.fullpvp.playermanager.PVPPlayerData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles player join events
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onLogin(CoreLoginEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        PVPPlayerData data = new PVPPlayerData(playerInfo);
        Main.getIns().getMysql().loadPlayerData(data);
        e.setPlayerInfo(data);
    }

}
