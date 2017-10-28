package com.nuno1212s.mercadonegro.events;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.inventories.CInventoryItem;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Map;

public class ConfirmInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        InventoryData confirmInventory = Main.getIns().getInventoryManager().getConfirmInventory();
        if (confirmInventory.equals(e.getInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onConfirm(InventoryClickEvent e) {
        InventoryData confirmInventory = Main.getIns().getInventoryManager().getConfirmInventory();
        if (confirmInventory.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() != null && confirmInventory.equals(e.getClickedInventory())) {

            e.setResult(Event.Result.DENY);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            InventoryItem item = confirmInventory.getItem(e.getSlot());

            InventoryItem item1 = confirmInventory.getItemWithFlag("ITEM");

            NBTCompound nbt = new NBTCompound(e.getInventory().getItem(item1.getSlot()));

            Map<String, Object> values = nbt.getValues();
            String id = (String) values.get("ID");
            int slot = (Integer) values.get("Slot");
            CInventoryItem sellInventory = (CInventoryItem) Main.getIns().getInventoryManager().getInventory(id).getItem(slot);

            if (sellInventory == null) {
                e.getWhoClicked().closeInventory();
                return;
            }

            if (item.hasItemFlag("CONFIRM")) {

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

                sellInventory.buyItem(playerData);

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildInventory(id));

            } else if (item.hasItemFlag("DENY")) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildInventory(id));
            }

        }

    }

}
