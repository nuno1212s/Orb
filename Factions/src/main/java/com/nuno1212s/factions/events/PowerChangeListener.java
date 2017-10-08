package com.nuno1212s.factions.events;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.MainData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PowerChangeListener implements Listener {

    @EventHandler
    public void onPowerChange(EventFactionsPowerChange e) {
        MPlayer player = e.getMPlayer();
        FPlayerData data = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(player.getUuid());

        MainData.getIns().getScheduler().runTaskLater(() -> {
            DisplayMain.getIns().getScoreboardManager().createScoreboard(data, data.getPlayerReference(Player.class));
        }, 1);
    }

}
