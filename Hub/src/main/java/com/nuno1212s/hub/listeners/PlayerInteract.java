package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.guis.options.PlayerVisibilityOption;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {
    public Main plugin;

    public PlayerInteract(Main pl) {
        this.plugin = pl;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!Main.getInstance().getConfig().getBoolean("CanBuild", false) || !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }

        Player p = e.getPlayer();
        Action action = e.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()) {

                e.setCancelled(true);

                ItemStack item = p.getItemInHand();

                if (PlayerVisibilityOption.getIns().checkItem(item)) {
                    int slot = p.getInventory().getHeldItemSlot();
                    PlayerVisibilityOption.getIns().changeOption(p);
                    p.getInventory().setItem(slot, PlayerVisibilityOption.getIns().getItem(p));
                }

            }

        }

    }


}
