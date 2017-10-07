package com.nuno1212s.hub.listeners;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

/**
 * Handles the player information loading event
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onCoreLogin(CoreLoginEvent e) {
        PlayerData playerInfo = e.getPlayerInfo();
        e.setPlayerInfo(Main.getIns().getMySqlManager().getPlayerData(playerInfo));
    }

    @EventHandler
    public void onHubJoin(PlayerJoinEvent e) {
        HPlayerData player = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        Map<Integer, ItemStack> items = Main.getIns().getHotbarManager().getItems(player);

        PlayerInventory inventory = e.getPlayer().getInventory();
        inventory.clear();
        items.forEach((slot, item) ->
            inventory.setItem(slot, item.clone())
        );

        Main.getIns().getPlayerToggleManager().handleJoin(player, e.getPlayer());

        MainData.getIns().getMessageManager().getMessage("JOIN").sendTo(e.getPlayer());
        if (e.getPlayer().hasPermission("joinMessage")) {
            MainData.getIns().getMessageManager().getMessage("PLAYER_JOINED")
                    .format("%player%", player.getNameWithPrefix())
                    .sendTo(Bukkit.getOnlinePlayers());
        }

        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());

    }

}
