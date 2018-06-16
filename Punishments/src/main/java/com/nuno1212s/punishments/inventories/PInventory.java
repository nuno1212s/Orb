package com.nuno1212s.punishments.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.punishments.main.Main;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PInventory extends InventoryData<PInventoryItem> {

    @Getter
    private static List<UUID> notRemove = new ArrayList<>();

    public PInventory(File f) {
        super(f, PInventoryItem.class, true, PInventory::onTransfer);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        e.setResult(Event.Result.DENY);

        PInventoryItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (!e.getWhoClicked().hasPermission(item.getPermission())) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(e.getWhoClicked());
            return;
        }

        item.applyToPlayer(Main.getIns().getInventoryManager().getTargetForPlayer(e.getWhoClicked().getUniqueId()));

        e.getWhoClicked().closeInventory();
    }

    private static void onTransfer(HumanEntity o) {

        notRemove.add(o.getUniqueId());

    }
}
