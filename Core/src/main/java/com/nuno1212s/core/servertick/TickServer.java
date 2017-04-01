package com.nuno1212s.core.servertick;

import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.playermanager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ticks random stuff
 */
public class TickServer implements Runnable {

    private Main m;

    public TickServer(Main m) {
        this.m = m;
    }

    @Override
    public void run() {
        List<UUID> toKick = new ArrayList<>();
        synchronized (PlayerManager.getIns().getPlayers()) {
            PlayerManager.getIns().getPlayers().forEach(player -> {
                if (System.currentTimeMillis() - player.getLastMovement() > 600000) {
                    //Kick Player
                    //toKick.add(player.getId());
                }
            });
        }

        Bukkit.getScheduler().runTask(m, () -> {
            toKick.forEach(toKickP -> {
                Player p = Bukkit.getPlayer(toKickP);
                if (p == null || !p.isOnline()) {
                    return;
                }

                p.kickPlayer(Main.getIns().getMessages().getMessage("KickAFK").toString());
            });
        });

    }
}
