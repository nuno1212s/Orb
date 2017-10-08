package com.nuno1212s.factions.events;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.MainData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionCreateListener implements Listener {

    @EventHandler
    public void onFactionCreate(EventFactionsCreate e) {
        MPlayer mPlayer = e.getMPlayer();
        FPlayerData player = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(mPlayer.getUuid());

        MainData.getIns().getScheduler().runTaskLater(() -> {
            DisplayMain.getIns().getScoreboardManager().createScoreboard(player, player.getPlayerReference(Player.class));
        }, 1);

    }

    @EventHandler
    public void onFactionDelete(EventFactionsDisband e) {
        MPlayer mPlayer = e.getMPlayer();
        FPlayerData player = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(mPlayer.getUuid());

        MainData.getIns().getScheduler().runTaskLater(() -> {
            DisplayMain.getIns().getScoreboardManager().createScoreboard(player, player.getPlayerReference(Player.class));
        }, 1);
    }

    @EventHandler
    public void onFactionChangeName(EventFactionsNameChange e) {
        MPlayer mPlayer = e.getMPlayer();
        FPlayerData player = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(mPlayer.getUuid());

        MainData.getIns().getScheduler().runTaskLater(() -> {
            DisplayMain.getIns().getScoreboardManager().createScoreboard(player, player.getPlayerReference(Player.class));
        }, 1);
    }

}
