package com.nuno1212s.machines.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.machines.machinemanager.MachineConfiguration;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.JSONObject;

import java.io.File;

public class MachineInventory extends InventoryData<MachineItem> {

    public MachineInventory(File jsonFile) {
        super(jsonFile, MachineItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        MachineItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (item.getItemFlags().contains("BUY")) {

            //TODO: confirm buy

        }
    }

}

class MachineItem extends InventoryItem {

    @Getter
    MachineConfiguration configuration;

    public MachineItem(JSONObject data) {
        super(data);

        configuration = new MachineConfiguration(data);
    }
}
