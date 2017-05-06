package com.nuno1212s.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles some item util methods
 */
public class ItemUtils {

    public static ItemStack formatItem(ItemStack itemToFormat, Map<String, String> placeHolders) {

        if (!itemToFormat.hasItemMeta()) {
            return itemToFormat;
        }

        ItemMeta itemMeta = itemToFormat.getItemMeta();

        if (itemMeta.hasDisplayName()) {
            String displayName = itemMeta.getDisplayName();

            for (Map.Entry<String, String> placeHoldersSet : placeHolders.entrySet()) {
                displayName = displayName.replace(placeHoldersSet.getKey(), placeHoldersSet.getValue());
            }

            itemMeta.setDisplayName(displayName);
        }

        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore(), newLore = new ArrayList<>();

            lore.forEach(l -> {
                placeHolders.forEach((key, value) ->
                    newLore.add(l.replace(key, value))
                );
            });

            itemMeta.setLore(newLore);
        }

        itemToFormat.setItemMeta(itemMeta);

        return itemToFormat;
    }

}
