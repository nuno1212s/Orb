package com.nuno1212s.vanish.vanishmanager;

import com.nuno1212s.vanish.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishManager {

    /**
     * Vanish a player from the server
     *
     * @param p
     */
    public void vanishPlayer(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId())) {
                continue;
            }

            if (player.hasPermission("overrideVanish")) {
                continue;
            }

            player.hidePlayer(p);
        }
    }

    /**
     * Un-vanish a player
     * @param p
     */
    public void unVanishPlayer(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId())) {
                continue;
            }

            if (!player.canSee(p)) {
                player.showPlayer(p);
            }
        }
    }

    /**
     * Handle a player joining
     * @param p
     */
    public void handlePlayerJoin(Player p) {

        boolean shouldHide = !p.hasPermission("overrideVanish");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(p.getUniqueId())) {
                continue;
            }

            if (Main.getIns().getPlayerManager().isPlayerVanished(p.getUniqueId()) && shouldHide) {
                p.hidePlayer(player);
            }
        }
    }

}
