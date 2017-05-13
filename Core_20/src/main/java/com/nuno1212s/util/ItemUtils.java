package com.nuno1212s.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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

    public static String itemTo64(ItemStack stack) throws IllegalStateException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);){
            dataOutput.writeObject(stack);

            // Serialize that array

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack itemFrom64(String data) throws IOException {
        try {

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
                 BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)){
                return (ItemStack) dataInput.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
