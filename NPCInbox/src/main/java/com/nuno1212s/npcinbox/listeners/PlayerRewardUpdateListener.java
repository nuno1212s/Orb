package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.events.PlayerRewardUpdateEvent;
import com.nuno1212s.npcinbox.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerRewardUpdateListener implements Listener {
    
    @EventHandler
    public void onReward(PlayerRewardUpdateEvent e) {
        Main.getIns().getNpcManager().displayNotificationsForPlayer(e.getPlayerInfo());
    }

}
