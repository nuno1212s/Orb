package com.nuno1212s.hub.guis;

import com.nuno1212s.core.permissions.PlayerPermissions;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProfileInventory implements Listener {

    public Main m;

    private static ProfileInventory ins;

    public static ProfileInventory getIns() {
        return ins;
    }

    public ProfileInventory(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();
        item = ConfigUtils.getIns().getItem("Hotbar.Profile.Item", fc);
        slot = fc.getInt("Hotbar.Profile.Slot", 3);
        invName = fc.getString("Inventory.Profile.Name", "Options");
        lines = fc.getInt("Inventory.Profile.InventoryLines", 3);

        ConfigurationSection cs = fc.getConfigurationSection("Inventory.Profile.Itens");
        Set<String> keys = cs.getKeys(false);
        for (String k : keys) {
            ItemStack item1 = ConfigUtils.getIns().getItem("Inventory.Profile.Itens." + k + ".Item", fc);
            int slot = fc.getInt("Inventory.Profile.Itens." + k + ".Slot", 0);
            this.itens.put(slot, item1);
        }

    }

    private ItemStack item;
    public int slot;
    private HashMap<Integer, ItemStack> itens = new HashMap<>();

    public ItemStack getItem(String player) {
        if (item.getType().equals(Material.SKULL_ITEM)) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(player);
            item.setItemMeta(meta);
        }
        return item;
    }

    private String invName;
    private int lines;

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, (9 * lines), invName);

        PlayerData pd = PlayerManager.getIns().getPlayerData(p.getUniqueId());
        for (int i : itens.keySet()) {
            ItemStack item = itens.get(i);
            ItemMeta im = item.getItemMeta();

            if (im.getDisplayName().contains("{CASH}"))
                im.setDisplayName(im.getDisplayName().replace("{CASH}", "" + pd.getCash()));

            if (im.getDisplayName().contains("{GROUP}"))
                im.setDisplayName(im.getDisplayName().replace("{GROUP}", "" + PlayerPermissions.getIns().getGroup(p).getDisplay()));

            List<String> lore = im.getLore();
            List<String> newLore = new ArrayList<>();
            for (String l : lore) {
                newLore.add(l.replace("{CASH}", "" + pd.getCash()).replace("{GROUP}", "" + PlayerPermissions.getIns().getGroup(p).getDisplay()));
            }

            im.setLore(newLore);
            item.setItemMeta(im);

            inv.setItem(i, itens.get(i));
        }

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

                if (getItem(p.getName()).getItemMeta().getDisplayName().equals(item1.getItemMeta().getDisplayName())) {
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


            }
        }
    }

}
