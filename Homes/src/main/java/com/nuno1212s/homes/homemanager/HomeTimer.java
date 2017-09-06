package com.nuno1212s.homes.homemanager;

import com.nuno1212s.homes.main.Main;
import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Home timers
 */
public class HomeTimer implements Runnable {

    private Map<UUID, HomeState> homes = new ConcurrentHashMap<>();

    public HomeTimer() {
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 10, 1);
    }

    public boolean registerTeleport(UUID u, Home h) {
        long timeNeeded = Main.getIns().getHomeManager().getTimeNeeded() * 1000;
        return homes.putIfAbsent(u, new HomeState(timeNeeded, timeNeeded, h.getLocation().clone())) == null;
    }

    public void cancelTeleport(UUID u) {
        if (homes.containsKey(u)) {
            homes.remove(u);
        }
    }

    public boolean isTeleporting(UUID u) {
        return this.homes.containsKey(u);
    }

    @Override
    public void run() {
        List<UUID> toRemove = new ArrayList<>();

        homes.forEach((u, w) -> {
            w.setTimeLeft(w.getTimeLeft() - 50);
            if (w.getTimeLeft() <= 0) {
                w.safeTeleport(u);
                toRemove.add(u);
            }
        });

        toRemove.forEach(homes::remove);
        toRemove.clear();
    }
}

@AllArgsConstructor
@Data
class HomeState {

    long timeNeeded, timeLeft;

    Location homeLocation;

    void safeTeleport(UUID u) {
        MainData.getIns().getScheduler().runTask(() -> {
            Player player = Bukkit.getPlayer(u);
            if (player == null || !player.isOnline()) {
                return;
            }
            player.teleport(homeLocation);
            MainData.getIns().getMessageManager().getMessage("TELEPORTED_HOME").sendTo(player);
        });
    }

}
