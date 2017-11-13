package com.nuno1212s.enderchest.enderchestmanager;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EnderChestManager {

    @Getter
    private String inventoryName;

    public EnderChestManager(Module m) {
        File configFile = m.getFile("config.json", true);

        try (FileReader r = new FileReader(configFile)) {

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(r);

            this.inventoryName = ChatColor.translateAlternateColorCodes('&',
                    (String) jsonObject.getOrDefault("InventoryName", "&cEnderChest"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the ender chest inventory
     *
     * @param playerData The data of the enderchestmanager
     * @return
     */
    public Inventory getEnderChestFor(Player p, EnderChestData playerData) {

        int enderChestSize = getSizeForPlayer(p);
        if (enderChestSize > playerData.getEnderChest().length) {
            playerData.updateEnderChestData(EnderChestData.expandInventory(playerData.getEnderChest(), enderChestSize));
        }

        Inventory inventory = Bukkit.getServer().createInventory(null, playerData.getEnderChest().length, inventoryName);

        inventory.setContents(playerData.getEnderChest());

        return inventory;
    }

    /**
     * Get the correct inventory
     *
     * @param p
     * @return
     */
    public int getSizeForPlayer(Player p) {
        if (p.hasPermission("ec.54")) {
            return 54;
        } else if (p.hasPermission("ec.45")) {
            return 45;
        } else if (p.hasPermission("ec.36")) {
            return 36;
        } else if (p.hasPermission("ec.27")) {
            return 27;
        }
        return 27;
    }

}
