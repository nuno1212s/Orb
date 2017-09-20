package com.nuno1212s.mercado.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.util.InventoryListener;
import com.nuno1212s.mercado.util.ItemIDUtils;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.UUID;

/**
 * Sell inventorylisteners handler
 */
public class SellInventoryListener extends InventoryListener {

    public SellInventoryListener() {
        super(Main.getIns().getMarketManager().getSellInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (getInventory().equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (e.getClickedInventory().getName().equalsIgnoreCase(e.getInventory().getName())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            InventoryItem item = getInventory().getItem(e.getSlot());

            if (item == null) {
                e.setResult(Event.Result.DENY);
                return;
            }

            if (!item.hasItemFlag("SELL_ITEM")) {
                e.setResult(Event.Result.DENY);
            }

            if (item.hasItemFlag("CURRENCY_TYPE")) {

                ItemStack item1 = e.getCurrentItem();

                if (item1.getType() == Main.getIns().getMarketManager().getCashItem().getType()) {
                    e.getClickedInventory().setItem(item.getSlot(), Main.getIns().getMarketManager().getCoinsItems().clone());
                } else if (item1.getType() == Main.getIns().getMarketManager().getCoinsItems().getType()) {
                    e.getClickedInventory().setItem(item.getSlot(), Main.getIns().getMarketManager().getCashItem().clone());
                }

            } else if (item.hasItemFlag("CONFIRM")) {

                InventoryItem sell_item = getInventory().getItemWithFlag("SELL_ITEM");
                ItemStack item1 = e.getClickedInventory().getItem(sell_item.getSlot());
                if (item1 != null && item1.getType() != Material.AIR) {
                    if (Main.getIns().getMarketManager().getActivePlayerItems(e.getWhoClicked().getUniqueId()).size() > 10) {
                        return;
                    }

                    Item sellItem = new Item(ItemIDUtils.getNewRandomID(),
                            e.getWhoClicked().getUniqueId(),
                            null,
                            item1,
                            0,
                            System.currentTimeMillis(),
                            0,
                            isServerCurrency(e.getClickedInventory().getItem(inventory.getItemWithFlag("CURRENCY_TYPE").getSlot())),
                            false);
                    addCallback(e.getWhoClicked().getUniqueId(), sellItem);

                    e.getWhoClicked().closeInventory();
                    MainData.getIns().getMessageManager().getMessage("INSERT_PRICE").sendTo(e.getWhoClicked());
                }

            } else if (item.hasItemFlag("CANCEL")) {
                InventoryItem sell_item = getInventory().getItemWithFlag("SELL_ITEM");
                ItemStack item1 = e.getClickedInventory().getItem(sell_item.getSlot());

                if (item1 != null && item1.getType() != Material.AIR) {
                    e.getWhoClicked().getWorld().dropItemNaturally(e.getWhoClicked().getLocation(), item1);
                }

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getMarketManager().getLandingInventory());
            }
        }
    }

    private boolean isServerCurrency(ItemStack i) {
        if (i.getType() == Main.getIns().getMarketManager().getCoinsItems().getType()) {
            return true;
        } else if (i.getType() == Main.getIns().getMarketManager().getCashItem().getType()) {
            return false;
        }
        return true;
    }

    /**
     * Adds the callback to the chat handler
     *
     * Handles the player disconnecting mid price typing {@link PlayerQuitListener#onQuit(PlayerQuitEvent)}
     *
     * @param player The owner of the item
     * @param item The item that is being placed
     */
    private void addCallback(UUID player, Item item) {
        Main.getIns().getMarketManager().getChatManager().addCallback(player, new Callback() {
            @Override
            public void callback(Object... args) {
                if (args.length > 0) {
                    if (args[0] instanceof String) {
                        long cost;
                        try {
                            cost = Long.parseLong((String) args[0]);
                            if (cost <= 0) {
                                MainData.getIns().getMessageManager().getMessage("COST_MUST_BE_POSITIVE").sendTo(Bukkit.getPlayer(player));
                                addCallback(player, item);
                                return;
                            }
                            item.setCost(cost);
                            Main.getIns().getMarketManager().addItem(item);
                            MainData.getIns().getMessageManager().getMessage("ANNOUNCED_ITEM")
                                    .format("%price%", NumberFormat.getInstance().format(cost))
                                    .format("%currency%", item.isServerCurrency() ? ChatColor.YELLOW + "coins" : ChatColor.GREEN + "cash").sendTo(Bukkit.getPlayer(player));

                            return;
                        } catch (NumberFormatException e) {}
                    } else if (args[0] instanceof Boolean && !(boolean) args[0]) {
                        //THIS MEANS THE PLAYER DISCONNECTED BEFORE WRITING THE PRICE
                        Player arg = (Player) args[1];
                        arg.getInventory().addItem(item.getItem());
                        return;
                    }
                }
                MainData.getIns().getMessageManager().getMessage("COST_MUST_BE_NUMBER").sendTo(Bukkit.getPlayer(player));
                addCallback(player, item);
            }
        });
    }

}
