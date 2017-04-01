package com.nuno1212s.hub.guis;

import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nuno1212s.hub.guis.options.ChatOption;
import com.nuno1212s.hub.guis.options.PlayerVisibilityOption;
import com.nuno1212s.hub.guis.options.TellOption;

public class OptionsInventory implements Listener {

    public Main m;

    private static OptionsInventory ins;

    public static OptionsInventory getIns() {
        return ins;
    }

    public OptionsInventory(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();
        item = ConfigUtils.getIns().getItem("Hotbar.Options.Item", fc);
        slot = fc.getInt("Hotbar.Options.Slot", 4);
        invName = fc.getString("Inventory.Options.Name", "Options");
        lines = fc.getInt("Inventory.Options.InventoryLines", 3);

        new TellOption(m);
        new PlayerVisibilityOption(m);
        new ChatOption(m);

    }

    public ItemStack item;
    public int slot;

    private String invName;
    private int lines;

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, (9 * lines), invName);

        PlayerData d = PlayerManager.getIns().getPlayerData(p.getUniqueId());

        TellOption tell = TellOption.getIns();
        inv.setItem(tell.slot, tell.getItem(d));

        ChatOption chat = ChatOption.getIns();
        inv.setItem(chat.slot, chat.getItem(d));

        p.openInventory(inv);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Action action = e.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()) {
                e.setCancelled(true);

                ItemStack item1 = p.getItemInHand();

                if (this.item.equals(item1)) {
                    openMenu(p);
                }

            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null) {
            return;
        }

        ItemStack item = e.getCurrentItem();

        if (e.getClickedInventory().getName().equalsIgnoreCase(invName)) {

            e.setResult(Event.Result.DENY);

            if (item == null || item.getType() == Material.AIR) {
                return;
            }

            if (item.hasItemMeta()) {

                PlayerData playerData = PlayerManager.getIns().getPlayerData(e.getWhoClicked().getUniqueId());

                if (TellOption.getIns().getItem(playerData).equals(item)) {
                    TellOption.getIns().changeOption(playerData);
                    e.getInventory().setItem(e.getSlot(), TellOption.getIns().getItem(playerData));
                }

                if (ChatOption.getIns().getItem(playerData).equals(item)) {
                    ChatOption.getIns().changeOption(playerData);
                    e.getInventory().setItem(e.getSlot(), ChatOption.getIns().getItem(playerData));
                }


            }
        }
    }
}
