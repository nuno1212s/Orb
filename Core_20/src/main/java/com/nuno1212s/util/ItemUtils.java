package com.nuno1212s.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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

    /**
     * Format the item with the given place holders
     *
     * Also formats the skull owner if the item is a skull
     *
     * @param itemToFormat
     * @param placeHolders
     * @return
     */
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

                for (Map.Entry<String, String> entries : placeHolders.entrySet()) {
                    l = l.replace(entries.getKey(), entries.getValue());
                }

                newLore.add(l);
            });

            itemMeta.setLore(newLore);
        }

        if (itemMeta instanceof SkullMeta) {

            placeHolders.forEach((key, value) -> {
                ((SkullMeta) itemMeta).setOwner(((SkullMeta) itemMeta).getOwner().replace(key, value));
            });

        }

        itemToFormat.setItemMeta(itemMeta);

        return itemToFormat;
    }

    public static String itemTo64(ItemStack stack) throws IllegalStateException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)){

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
