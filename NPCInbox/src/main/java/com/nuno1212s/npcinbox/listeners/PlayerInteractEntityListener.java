package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Handles player right clicking NPC entities
 */
public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEntityEvent e) {
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());
        if (Main.getIns().getNpcManager().isNPCRegistered(npc.getUniqueId())) {
            e.setCancelled(true);
            PlayerData data = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            e.getPlayer().openInventory(Main.getIns().getInventoryManager().buildRewardInventoryForPlayer(data));
        }
    }

}
