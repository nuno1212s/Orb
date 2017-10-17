package com.nuno1212s.warps.tpamanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.timers.Teleport;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

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

    /**
     * The time needed to teleport the player
     *
     * @return The amount of time needed for a teleport to occur
     */
    @Override
    public long getTimeNeeded() {
        return timeNeeded;
    }

    /**
     * Get the location to teleport to
     *
     * @return The location to teleport to
     */
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
        this.hasAccepted = true;
        Player senderPlayer = this.getSender().getPlayerReference(Player.class),
            targetPlayer = this.getTarget().getPlayerReference(Player.class);

        if (senderPlayer == null || targetPlayer == null) {
            return;
        }

        if (getTimeNeeded() > 0) {
            MainData.getIns().getMessageManager().getMessage("TPA_ACCEPTED_TELEPORTING_IN")
                    .format("%time%", String.valueOf(this.getTimeNeeded())).sendTo(targetPlayer);
        } else {
            MainData.getIns().getMessageManager().getMessage("TPA_ACCEPTED_TELEPORTING_INSTANT")
                    .sendTo(targetPlayer);
        }

        MainData.getIns().getMessageManager().getMessage("TPA_ACCEPTED").sendTo(senderPlayer);
        Main.getIns().getTeleportTimer().registerTeleport(target.getPlayerID(), this);
    }

    /**
     * Notify the players that the teleport has been cancelled
     */
    public void notifyCancel() {
        Player senderPlayer = this.getSender().getPlayerReference(Player.class),
                targetPlayer = this.getTarget().getPlayerReference(Player.class);

        if (senderPlayer == null || targetPlayer == null) {
            return;
        }

        MainData.getIns().getMessageManager().getMessage("TPA_CANCELLED").sendTo(senderPlayer);
        MainData.getIns().getMessageManager().getMessage("TPA_SELF_CANCELLED").sendTo(targetPlayer);

    }

    /**
     * Check if this teleport request has expired
     * @return
     */
    public boolean hasExpired() {
        return this.getOriginalTime() + TimeUnit.MINUTES.toMillis(2) < System.currentTimeMillis();
    }

    public enum TeleportType {
        TPA,
        TPHERE
    }

}
