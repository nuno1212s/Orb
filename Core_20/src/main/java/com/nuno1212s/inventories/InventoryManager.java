package com.nuno1212s.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import org.bukkit.entity.HumanEntity;
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

                        e.getWhoClicked().openInventory(inventory.buildInventory());

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
