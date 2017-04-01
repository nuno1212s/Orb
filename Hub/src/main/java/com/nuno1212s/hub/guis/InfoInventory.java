package com.nuno1212s.hub.guis;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InfoInventory implements Listener {

    public Main m;

    private static InfoInventory ins;

    public static InfoInventory getIns() {
        return ins;
    }

    public InfoInventory(Main m) {
        this.m = m;
        this.ins = this;

        item = ConfigUtils.getIns().getItem("Hotbar.Info.Item", m.getConfig());
        slot = m.getConfig().getInt("Hotbar.Info.Slot", 7);
        msgs = m.getConfig().getStringList("Inventory.Info.Messages");
    }

    public ItemStack item;
    public int slot;
    private List<String> msgs;

    public void sendMessage(Player p) {
        for (String s : msgs)
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Action action = e.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()) {
                e.setCancelled(true);

                ItemStack item1 = p.getItemInHand();

                if (item1.getItemMeta().getDisplayName().contains(this.item.getItemMeta().getDisplayName())) {
                    sendMessage(p);
                }

            }

        }
    }

}
