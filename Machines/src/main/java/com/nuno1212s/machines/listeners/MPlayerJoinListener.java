package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.players.MachinePlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MPlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        PlayerData data = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        if (data instanceof MachinePlayer) {

            if (((MachinePlayer) data).getAmountMadeWhileAway() > 0) {

                MainData.getIns().getMessageManager().getMessage("MACHINE_MADE_WHILE_AWAY")
                        .format("%amount%", ((MachinePlayer) data).getAmountMadeWhileAway())
                        .sendTo(e.getPlayer());

            }

        }

    }

}
