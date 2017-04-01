package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HubListeners implements Listener {

    public Main plugin;

    public HubListeners(Main pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().isOp() && e.getClickedInventory() != null && e.getClickedInventory().getName().equals(e.getWhoClicked().getInventory().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
        e.getItemDrop().remove();
    }

    @EventHandler
    public void PlayerDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);

            Player p = (Player) e.getEntity();

            if (e.getCause() == DamageCause.VOID) {
                p.teleport(p.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void leaveDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!Main.getInstance().getConfig().getBoolean("CanBuild", false) || !e.getPlayer().isOnline()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!Main.getInstance().getConfig().getBoolean("CanBuild", false) || !e.getPlayer().isOnline()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (e.getAction().equals(Action.PHYSICAL))
            e.setCancelled(true);

    }

}
