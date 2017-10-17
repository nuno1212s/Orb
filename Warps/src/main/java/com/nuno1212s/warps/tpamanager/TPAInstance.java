package com.nuno1212s.warps.tpamanager;

import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.timers.Teleport;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TPAInstance implements Teleport {

    /**
     * Use PlayerData classes to avoid using player classes as this is run async
     */
    @Getter
    private PlayerData sender, target;

    @Getter
    private long timeNeeded, originalTime;

    private TeleportType type;

    @Getter
    @Setter
    private boolean hasAccepted;

    public TPAInstance(PlayerData toTeleport, PlayerData target, long timeNeeded, TeleportType teleportType) {
        this.sender = toTeleport;
        this.target = target;
        this.timeNeeded = timeNeeded;
        this.originalTime = System.currentTimeMillis();
        this.type = teleportType;
        this.hasAccepted = false;
    }

    @Override
    public long getTimeNeeded() {
        return timeNeeded;
    }

    @Override
    public Location getLocation() {
        if (type == TeleportType.TPA) {
            return target.getPlayerReference(Player.class).getLocation();
        } else if (type == TeleportType.TPHERE) {
            return sender.getPlayerReference(Player.class).getLocation();
        }

        return null;
    }

    /**
     * Start the teleport
     */
    public void start() {
        Main.getIns().getTeleportTimer().registerTeleport(target.getPlayerID(), this);
    }

    public enum TeleportType {
        TPA,
        TPHERE
    }

}
