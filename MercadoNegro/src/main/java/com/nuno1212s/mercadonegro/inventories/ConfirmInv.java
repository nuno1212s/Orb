package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.Map;

public class ConfirmInv extends InventoryData<InventoryItem> {

    public ConfirmInv(File jsonFile, Class<InventoryItem> itemClass, boolean directRedirect) {
        super(jsonFile, itemClass, directRedirect);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        InventoryItem item = getItem(e.getSlot());

        InventoryItem item1 = getItemWithFlag("ITEM");

        if (item == null || item1 == null) {
            return;
        }

        NBTCompound nbt = new NBTCompound(e.getInventory().getItem(item1.getSlot()));

        Map<String, Object> values = nbt.getValues();
        String id = (String) values.get("ID");
        int slot = (Integer) values.get("Slot");

        CInventoryItem sellInventory = Main.getIns().getInventoryManager().getInventory(id).getItem(slot);

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
