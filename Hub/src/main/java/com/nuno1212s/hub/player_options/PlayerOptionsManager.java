package com.nuno1212s.hub.player_options;

import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Manages player options
 */
public class PlayerOptionsManager {

    @Getter
    private InventoryData optionsInventory;

    private ItemStack toggleOn, toggleOff;

    public PlayerOptionsManager(Module m) {

        this.optionsInventory = new InventoryData(m.getFile("optionsInv.json", true), null);
        File file = m.getFile("items.json", true);

        JSONObject items;

        try (Reader r = new FileReader(file)) {
            items = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        this.toggleOn = new SerializableItem((JSONObject) items.get("On"));
        this.toggleOff = new SerializableItem((JSONObject) items.get("Off"));
    }

    /**
     * Get the options inventory for a player
     *
     * @param d The player
     * @return
     */
    public Inventory getInventoryForPlayer(HPlayerData d) {
        Inventory inventory = optionsInventory.buildInventory();

        InventoryItem tellToggle = optionsInventory.getItemWithFlag("TELL_TOGGLE");
        if (tellToggle != null) {
            if (d.isTell()) {
                inventory.setItem(tellToggle.getSlot(), toggleOn);
            } else {
                inventory.setItem(tellToggle.getSlot(), toggleOff);
            }
        }

        InventoryItem chatToggle = optionsInventory.getItemWithFlag("CHAT_TOGGLE");
        if (chatToggle != null) {
            if (d.isChatEnabled()) {
                inventory.setItem(chatToggle.getSlot(), toggleOn);
            } else {
                inventory.setItem(chatToggle.getSlot(), toggleOff);
            }
        }

        return inventory;
    }

}
