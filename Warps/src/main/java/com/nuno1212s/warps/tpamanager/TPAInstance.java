package com.nuno1212s.warps.tpamanager;

import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.warps.timers.Teleport;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TPAInstance implements Teleport {

    /**
     * Use PlayerData classes to avoid using player classes as this is run async
     */
    private PlayerData toTeleport, target;

    private long timeNeeded;

    public TPAInstance(PlayerData toTeleport, PlayerData target) {
        this.toTeleport = toTeleport;
        this.target = target;
    }

    @Override
    public long getTimeNeeded() {
        return timeNeeded;
    }

    @Override
    public Location getLocation() {
        return target.getPlayerReference(Player.class).getLocation();
    }
}
