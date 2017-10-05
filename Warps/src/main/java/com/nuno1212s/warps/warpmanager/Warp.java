package com.nuno1212s.warps.warpmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.SerializableLocation;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.timers.Teleport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 * Warp data class
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Warp implements Teleport {

    private String warpName;

    private Location l;

    private String permission;

    private boolean delay, requiredConsole;

    private int delayInSeconds;

    @Override
    public Location getLocation() {
        return l;
    }

    @Override
    public long getTimeNeeded() {
        return delayInSeconds;
    }

    public void teleport(Player p) {

        if (!p.hasPermission(getPermission())) {
            MainData.getIns().getMessageManager().getMessage("WARPS_NO_PERMISSION").sendTo(p);
            return;
        }


        if (Main.getIns().getTeleportTimer().isTeleporting(p.getUniqueId())) {
            Main.getIns().getTeleportTimer().cancelTeleport(p.getUniqueId());
            MainData.getIns().getMessageManager().getMessage("TELEPORT_CANCELLED_ANOTHER_TELEPORT").sendTo(p);
        }

        if (isDelay() && !p.hasPermission("warps.instant")) {
            Main.getIns().getTeleportTimer().registerTeleport(p.getUniqueId(), this);
            MainData.getIns().getMessageManager().getMessage("WARPS_WARPING_IN").format("%time%", String.valueOf(getDelayInSeconds())).sendTo(p);
        } else {
            p.teleport(getL());
            MainData.getIns().getMessageManager().getMessage("WARPS_WARPED").sendTo(p);
        }
    }

}
