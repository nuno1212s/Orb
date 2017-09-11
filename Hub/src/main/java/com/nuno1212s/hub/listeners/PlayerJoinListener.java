package com.nuno1212s.hub.listeners;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.playermanager.PlayerData;
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
        e.setPlayerInfo(new HPlayerData(playerInfo));
    }

    @EventHandler
    public void onHubJoin(PlayerJoinEvent e) {
        Map<Integer, ItemStack> items = Main.getIns().getHotbarManager().getItems();

        PlayerInventory inventory = e.getPlayer().getInventory();
        items.forEach((slot, item) ->
            inventory.setItem(slot, item.clone())
        );

    }

}
