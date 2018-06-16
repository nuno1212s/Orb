package com.nuno1212s.hub.player_options;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.File;

public class PlayerOptionsInv extends InventoryData<PlayerOptionsItem> {


    public PlayerOptionsInv(File jsonFile, Class<PlayerOptionsItem> itemClass, boolean directRedirect) {
        super(jsonFile, itemClass, directRedirect);
    }

    public Inventory buildInventory() {

        Inventory i = Bukkit.getServer().createInventory(null, this.getInventorySize(), this.getInventoryName());

        for (PlayerOptionsItem item : items) {

            if (!item.isToggleable())
                i.setItem(item.getSlot(), item.getItem().clone());
        }

        //TODO: Build inventory
        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        e.setResult(Event.Result.DENY);

        InventoryItem item = getItem(e.getSlot());
        if (item != null) {
            if (item.hasItemFlag("TELL_TOGGLE")) {
                HPlayerData d = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                d.setTell(!d.isTell());

                Main.getIns().getRedisHandler().publishTellUpdate(d);
                Inventory playerInventory = Main.getIns().getPlayerOptionsManager().getInventoryForPlayer(d);
                e.getClickedInventory().setContents(playerInventory.getContents());
            } else if (item.hasItemFlag("CHAT_TOGGLE")) {
                HPlayerData d = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                d.setChatEnabled(!d.isChatEnabled());

                Inventory playerInventory = Main.getIns().getPlayerOptionsManager().getInventoryForPlayer(d);
                e.getClickedInventory().setContents(playerInventory.getContents());
            }
        }


    }
}

class PlayerOptionsItem extends InventoryItem {

    @Getter
    private ItemStack on, off;

    @Getter
    private boolean toggleable;

    public PlayerOptionsItem(JSONObject data) {

        super(data);

        toggleable = (boolean) data.getOrDefault("Toggleable", false);

        if (toggleable) {
            //TODO: Read on / off
            this.on = new SerializableItem((JSONObject) data.get("On"));
            this.off = new SerializableItem((JSONObject) data.get("Off"));
        }

    }

}