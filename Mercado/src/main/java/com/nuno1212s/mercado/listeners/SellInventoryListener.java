package com.nuno1212s.mercado.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.util.InventoryListener;
import com.nuno1212s.mercado.util.ItemIDUtils;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import com.nuno1212s.util.Callback;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Sell inventory handler
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
        }

        if (e.getClickedInventory().getName().equalsIgnoreCase(e.getInventory().getName())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryItem item = getInventory().getItem(e.getSlot());
            if (item == null) {
                return;
            }

            if (item.hasItemFlag("CURRENCY_TYPE")) {
                //TODO: Change the currency type
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
                            0,
                            0,
                            false,
                            false);
                    addCallback((Player) e.getWhoClicked(), sellItem);
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

    private void addCallback(Player player, Item item) {
        Main.getIns().getMarketManager().getChatManager().addCallback(player.getUniqueId(), new Callback() {
            @Override
            public void callback(Object... args) {
                if (args.length > 0) {
                    if (args[0] instanceof String) {
                        long cost;
                        try {
                            cost = Long.parseLong((String) args[0]);
                            item.setCost(cost);
                            return;
                        } catch (NumberFormatException e) {}
                    }
                }
                MainData.getIns().getMessageManager().getMessage("COST_MUST_BE_NUMBER").sendTo(player);
                addCallback(player, item);
            }
        });
    }

}
