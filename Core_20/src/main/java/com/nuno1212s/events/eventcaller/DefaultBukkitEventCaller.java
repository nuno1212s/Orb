package com.nuno1212s.events.eventcaller;

import com.nuno1212s.events.PlayerGroupUpdateEvent;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.events.PlayerRewardUpdateEvent;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;

public class DefaultBukkitEventCaller implements EventCaller {

    @Override
    public void callGroupUpdateEvent(PlayerData data, Group previousGroup) {
        Bukkit.getServer().getPluginManager().callEvent(new PlayerGroupUpdateEvent(data, previousGroup));
    }

    @Override
    public void callUpdateInformationEvent(PlayerData args) {
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(args));
    }

    @Override
    public void callRewardsUpdateEvent(PlayerData data) {
        Bukkit.getServer().getPluginManager().callEvent(new PlayerRewardUpdateEvent(data));
    }
}
