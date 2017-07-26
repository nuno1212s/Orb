package com.nuno1212s.warps.warpmanager;

import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WarpTimer implements Runnable {

    public WarpTimer() {
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 10, 1);
    }

    private ConcurrentMap<UUID, WarpState> map = new ConcurrentHashMap<>();

    public boolean registerWarp(UUID u, Warp w) {
        return map.putIfAbsent(u, new WarpState(w.getDelayInSeconds() * 1000, w.getDelayInSeconds() * 1000, w.getL().clone())) == null;
    }

    public void cancelWarp(UUID u) {
        if (map.containsKey(u)) {
            map.remove(u);
        }
    }

    public boolean isWarping(UUID u) {
        return this.map.containsKey(u);
    }

    @Override
    public void run() {
        List<UUID> toRemove = new ArrayList<>();
        map.forEach((u, w) -> {
            w.setTimeLeft(w.getTimeLeft() - 50);
            if (w.getTimeLeft() <= 0) {
                w.safeTeleport(u);
                toRemove.add(u);
            }
        });
        toRemove.forEach(map::remove);
        toRemove.clear();
    }
}

@AllArgsConstructor
@Data
class WarpState {

    long timeNeeded, timeLeft;

    Location teleportLocation;

    void safeTeleport(UUID u) {
        MainData.getIns().getScheduler().runTask(() -> {
            Player player = Bukkit.getPlayer(u);
            if (player == null || !player.isOnline()) {
                return;
            }
            player.teleport(teleportLocation);
            MainData.getIns().getMessageManager().getMessage("WARPED").sendTo(player);
        });
    }

}

