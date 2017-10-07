package com.nuno1212s.factions.events;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.events.PlayerInformationLoadEvent;
import com.nuno1212s.factions.main.Main;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {

    @EventHandler
    public void onLogin(CoreLoginEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        FPlayerData data = Main.getIns().getMysql().getPlayerData(playerInfo);
        e.setPlayerInfo(data);
    }

    @EventHandler
    public void onPlayerInfoLoad(PlayerInformationLoadEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        FPlayerData data = Main.getIns().getMysql().getPlayerData(playerInfo);
        e.setPlayerInfo(data);
    }


}
