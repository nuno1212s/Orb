package com.nuno1212s.mercado.util;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.util.inventories.InventoryData;
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
 * Handles SInventory Clicks
 */
public abstract class SInventoryListener implements Listener {

    @Getter(value = AccessLevel.PROTECTED)
    protected List<UUID> closeExceptions;

    public SInventoryListener() {
        this.closeExceptions = new ArrayList<>();
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
            if (Main.getIns().getMarketManager().getSearchManager().getInventoryByName(e.getInventory().getName()) != null) {
                Main.getIns().getMarketManager().removePage(e.getPlayer().getUniqueId());
            }
        } else {
            closeExceptions.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getMarketManager().getSearchManager().getInventoryByName(e.getInventory().getName()) != null) {
            e.setResult(Event.Result.DENY);
        }
    }

}
