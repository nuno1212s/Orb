package com.nuno1212s.mercado.util;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.inventories.InventoryData;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Inventory listener class
 */
public abstract class InventoryListener implements Listener {

    @Getter(value = AccessLevel.PROTECTED)
    protected List<UUID> closeExceptions;

    @Getter(value = AccessLevel.PROTECTED)
    protected InventoryData inventory;

    public InventoryListener(InventoryData inv) {
        this.closeExceptions = new ArrayList<>();
        this.inventory = inv;
    }

    protected void addCloseException(UUID player) {
        this.closeExceptions.add(player);
    }

    protected int getPageForPlayer(UUID player) {
        return Main.getIns().getMarketManager().getPage(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!closeExceptions.contains(e.getPlayer().getUniqueId())) {
            if (inventory.equals(e.getInventory())) {
                Main.getIns().getMarketManager().removePage(e.getPlayer().getUniqueId());
            }
        } else {
            closeExceptions.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (inventory.equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

}
