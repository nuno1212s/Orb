package com.nuno1212s.mercado.util.inventories;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles inventory data
 */
public class InventoryData {

    String inventoryName;

    int inventorySize;

    List<InventoryItem> items;

    public InventoryData(File jsonFile) {
        JSONObject jsOB;

        try (Reader r = new FileReader(jsonFile)) {
            jsOB = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            System.out.println("Failed to read the JSON File " + jsonFile.getName());
            return;
        }

        this.inventoryName = ChatColor.translateAlternateColorCodes('&', (String) jsOB.get("InventoryName"));
        this.inventorySize = ((Long) jsOB.get("InventorySize")).intValue();
        JSONArray inventoryItems = (JSONArray) jsOB.get("InventoryItems");
        this.items = new ArrayList<>(inventoryItems.size());
        inventoryItems.forEach((inventoryItem) -> {

        });
    }

}
