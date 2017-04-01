package com.nuno1212s.hub.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    private static ConfigUtils ins = new ConfigUtils();

    public static ConfigUtils getIns() {return ins;}

    public Location getLocation(String key, FileConfiguration fc) {

        String t = fc.getString(key);
        if (t == null) {
            return null;
        }
        if (!fc.contains(key)) {
            return null;
        }
        t = t.substring(1, t.length() - 1);

        String[] args = t.split(", ");
        String world = args[0];

        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double yaw = 0.0;
        double pitch = 0.0;

        try {

            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
            yaw = Double.parseDouble(args[4]);
            pitch = Double.parseDouble(args[5]);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return new Location(Bukkit.getWorld(world), x, y, z, (float) yaw, (float) pitch);
    }

    public void setLocation(String key, Location l, FileConfiguration fc) {
        String location = "(" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ", "
                + l.getYaw() + ", " + l.getPitch() + ")";
        fc.set(key, location);

    }

    public ItemStack getItem(String key, FileConfiguration fc) {
        String itemmaterials = fc.getString(key + ".ItemMaterial", "1:0");
        int amount = fc.getInt(key + ".Amount", 1);
        @SuppressWarnings("deprecation")
        ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(itemmaterials.split(":")[0])), amount, (byte) Integer.parseInt(itemmaterials.split(":")[1]));
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', fc.getString(key + ".DisplayName", item.getType().name())));
        List<String> olore = fc.getStringList(key + ".Lore");
        List<String> lore = new ArrayList<>();
        for (String s : olore)
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        im.setLore(lore);
        if (fc.getBoolean(key + ".Glow", false)) {
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (fc.getBoolean(key + ".HideAttributes", false)) {
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        item.setItemMeta(im);
        return item;
    }

}
