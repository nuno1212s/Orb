package com.nuno1212s.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager implements Listener {

    private Map<String, InventoryData> inventories;

    public InventoryManager() {

        if (MainData.getIns().isBungee()) {

            System.out.println("Tried to initialize inventory manager in bungee?");

            return;
        }

        inventories = new HashMap<>();

    }

    public InventoryData getInventory(String inventoryID) {
        return inventories.getOrDefault(inventoryID, null);
    }

    public InventoryData getInventoryByName(String inventoryName) {

        for (InventoryData inventoryData : inventories.values()) {
            if (inventoryData.getInventoryName().equalsIgnoreCase(inventoryName)) {
                return inventoryData;
            }
        }

        return null;
    }

    public boolean registerInventory(InventoryData data) {
        if (inventories.containsKey(data.getInventoryID())) {

            System.out.println("InventoryID: " + data.getInventoryID() + " is repeated. pls fix");

            return false;
        }

        inventories.put(data.getInventoryID(), data);
        return true;
    }

    /**
     * Remove the inventory from the inventory registers
     * @param data
     */
    public void removeInventory(InventoryData data) {

        this.inventories.remove(data.getInventoryID());

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (getInventoryByName(e.getInventory().getName()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getClickedInventory() == null) return;

        InventoryData inventoryByName = getInventoryByName(e.getInventory().getName());

        if (inventoryByName == null) {
            return;
        }

        if (inventoryByName.equals(e.getClickedInventory())) {

            if (inventoryByName.isDirectRedirect()) {

                InventoryItem item = inventoryByName.getItem(e.getSlot());

                if (item != null) {
                    if (item.getConnectingInv() != null) {

                        e.setResult(Event.Result.DENY);

                        Callback<HumanEntity> transferFunction = inventoryByName.getTransferFunction();

                        if (transferFunction != null) {

                            transferFunction.callback(e.getWhoClicked());

                        }

                        e.getWhoClicked().closeInventory();

                        InventoryData inventory = getInventory(item.getConnectingInventory());

                        if (inventory == null) {
                            return;
                        }

                        if (inventoryByName.getOpenFuction() != null) {
                            inventoryByName.getOpenFuction().callback(new Pair<>(e.getWhoClicked(), inventory));
                        } else {
                            e.getWhoClicked().openInventory(inventory.buildInventory((Player) e.getWhoClicked()));
                        }

                        return;
                    } else if (!item.getCommands().isEmpty()) {

                        e.setResult(Event.Result.DENY);

                        e.getWhoClicked().closeInventory();

                        item.getCommands().forEach(((Player) e.getWhoClicked())::performCommand);

                        return;
                    }
                }
            }

            inventoryByName.handleClick(e);
        } else {

            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }

        }

    }
}
