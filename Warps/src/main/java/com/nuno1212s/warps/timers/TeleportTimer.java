package com.nuno1212s.warps.timers;

import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles teleports
 */
public class TeleportTimer implements Runnable {


    private Map<UUID, TeleportState> teleports = new ConcurrentHashMap<>();

    public TeleportTimer() {
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 10, 1);
    }

    public boolean registerTeleport(UUID u, Teleport h) {
        long timeNeeded = h.getTimeNeeded() * 1000;
        return teleports.putIfAbsent(u, new TeleportState(timeNeeded, timeNeeded, h.getLocation().clone())) == null;
    }

    public void cancelTeleport(UUID u) {
        if (teleports.containsKey(u)) {
            teleports.remove(u);
        }
    }

    public boolean isTeleporting(UUID u) {
        return this.teleports.containsKey(u);
    }

    @Override
    public void run() {
        List<UUID> toRemove = new ArrayList<>();

        teleports.forEach((u, w) -> {
            w.setTimeLeft(w.getTimeLeft() - 50);
            if (w.getTimeLeft() <= 0) {
                w.safeTeleport(u);
                toRemove.add(u);
            }
        });

        toRemove.forEach(teleports::remove);
        toRemove.clear();
    }
}


@AllArgsConstructor
@Data
class TeleportState {

    long timeNeeded, timeLeft;

    Location teleportLocation;

    void safeTeleport(UUID u) {
        MainData.getIns().getScheduler().runTask(() -> {
            Player player = Bukkit.getPlayer(u);
            if (player == null || !player.isOnline()) {
                return;
            }
            player.teleport(teleportLocation);
            MainData.getIns().getMessageManager().getMessage("TELEPORTED").sendTo(player);
        });
    }

}

