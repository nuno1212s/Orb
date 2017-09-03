package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.inventories.InventoryBuilder;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Handles inventory closing events
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryCloseEvent e) {
        if (Main.getIns().getInventoryManager().getPlayerInventoryBuilder(e.getPlayer().getUniqueId()) != null) {
            Main.getIns().getInventoryManager().unregisterPlayer(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onConfirm(InventoryClickEvent e) {
        InventoryBuilder playerInventoryBuilder = Main.getIns().getInventoryManager().getPlayerInventoryBuilder(e.getWhoClicked().getUniqueId());
        if (playerInventoryBuilder != null) {
            if (e.getCurrentItem() != null) {
                if (InventoryBuilder.isConfirm(e.getCurrentItem())) {
                    e.setResult(Event.Result.DENY);
                    MainData.getIns().getRewardManager().createReward(playerInventoryBuilder.buildReward());
                    e.getWhoClicked().sendMessage(ChatColor.GREEN + "Created reward successfully");
                    Main.getIns().getInventoryManager().unregisterPlayer(e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().closeInventory();
                } else if (InventoryBuilder.isDeny(e.getCurrentItem())) {
                    e.setResult(Event.Result.DENY);
                    e.getWhoClicked().sendMessage(ChatColor.GREEN + "Canceled reward successfully");
                    Main.getIns().getInventoryManager().unregisterPlayer(e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().closeInventory();
                }
            }
        } else {
            InventoryData mainInventory = Main.getIns().getInventoryManager().getMainInventory();
            if (mainInventory.equals(e.getInventory())) {
                if (e.getClick().isShiftClick()) {
                    e.setResult(Event.Result.DENY);
                }
            } else {
                return;
            }

            if (e.getClickedInventory() == null) {
                return;
            }

            if (mainInventory.equals(e.getClickedInventory())) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                e.setResult(Event.Result.DENY);

                if (mainInventory.getItem(e.getSlot()) != null) {
                    return;
                }

                int rewardID = Main.getIns().getInventoryManager().getEmbeddedReward(e.getCurrentItem());
                Reward r = MainData.getIns().getRewardManager().getReward(rewardID);

                PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                r.deliver((Player) e.getWhoClicked(), player);

                e.getClickedInventory().setContents(Main.getIns().getInventoryManager().buildRewardInventoryForPlayer(player).getContents());
            }

        }
    }

}
