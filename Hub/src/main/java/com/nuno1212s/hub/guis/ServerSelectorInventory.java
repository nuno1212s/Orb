package com.nuno1212s.hub.guis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.nuno1212s.core.serverstatus.Status;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.messagemanager.Messages;
import com.nuno1212s.hub.servermanager.NovusServer;
import com.nuno1212s.hub.servermanager.ServerManager;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class ServerSelectorInventory implements Listener {

    public Main m;

    private static ServerSelectorInventory ins;

    public static ServerSelectorInventory getIns() {
        return ins;
    }

    public ServerSelectorInventory(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();

        item = ConfigUtils.getIns().getItem("Hotbar.ServerSelector.Item", fc);
        slot = fc.getInt("Hotbar.ServerSelector.Slot", 1);
        invName = fc.getString("Inventory.ServerSelector.Name", "Server Selector");
        int lines = fc.getInt("Inventory.ServerSelector.InventoryLines", 3);

        inv = Bukkit.createInventory(null, (9 * lines), invName);

        ConfigurationSection itens = fc.getConfigurationSection("Inventory.ServerSelector.Itens");
        Set<String> keys = itens.getKeys(false);
        for (String key : keys) {
            ItemStack item1 = ConfigUtils.getIns().getItem("Inventory.ServerSelector.Itens." + key + ".Item", fc);
            String bungeeId = fc.getString("Inventory.ServerSelector.Itens." + key + ".BungeeId", "none");
            if (!bungeeId.equalsIgnoreCase("none")) {
                if (ServerManager.getIns().getServerByBungeeID(bungeeId) == null) {
                    System.out.println("Server with bungeeid '" + bungeeId + "' not found in method load (ServerSelectorInventory)");
                    continue;
                }

                NovusServer ns = ServerManager.getIns().getServerByBungeeID(bungeeId);

                servers.put(key, ns);
                if (item1.getItemMeta().getDisplayName().contains("{SERVERNAME}")) {
                    ItemMeta im = item1.getItemMeta();
                    im.setDisplayName(im.getDisplayName().replace("{SERVERNAME}", ns.getDisplayName()));
                    item1.setItemMeta(im);
                }
            }
            lores.put(key, item1.getItemMeta().getLore());
            int slot = fc.getInt("Inventory.ServerSelector.Itens." + key + ".Slot", 0);
            slots.put(key, slot);
            inv.setItem(slot, item1);
        }

    }

    private String invName;
    public ItemStack item;
    public int slot;

    public Inventory inv;

    public void openMenu(Player p) {
        p.openInventory(inv);
    }

    private HashMap<String, Integer> slots = new HashMap<>();
    private HashMap<String, NovusServer> servers = new HashMap<>();
    private HashMap<String, List<String>> lores = new HashMap<>();

    public void updateMenu() {

        if (servers.size() == 0) {
            return;
        }

        for (String c : servers.keySet()) {

            ItemStack i = inv.getItem(slots.get(c));

            if (i == null || !i.hasItemMeta())
                continue;

            NovusServer ns = servers.get(c);

            List<String> finallore = new ArrayList<String>();
            List<String> lore = lores.get(c);
            for (String s : lore) {
                if (s.contains("{STATUS}"))
                    s = s.replace("{STATUS}", ns.getStatus());
                finallore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            ItemMeta im = i.getItemMeta();
            im.setLore(finallore);
            i.setItemMeta(im);

        }

    }

    public NovusServer getServer(int slot) {
        for (String a : slots.keySet())
            if (servers.containsKey(a))
                if (slots.get(a).equals(slot))
                    return servers.get(a);
        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Action action = e.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()) {
                e.setCancelled(true);

                ItemStack item1 = p.getItemInHand();

                if (item1.equals(this.item)) {
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

                NovusServer ns = getServer(e.getSlot());
                if (ns != null) {

                    if (ns.getInfo().getS() == Status.OFFLINE) {
                        p.sendMessage(Messages.getIns().getMessage("ServerOffline", "&cO servidor está offline!"));
                        return;
                    }

                    if (ns.getInfo().getCurrentPlayers() >= ns.getInfo().getMaxPlayers() && !p.hasPermission("novus.joinfull")) {
                        p.sendMessage(Messages.getIns().getMessage("ServerFull", "&6Servidor cheio... Compra VIP para teres slot reservado."));
                        return;
                    }
                    try {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);

                        out.writeUTF("Connect");
                        out.writeUTF(ns.getInfo().getServerName());

                        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }

    }

}
