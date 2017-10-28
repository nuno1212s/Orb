package com.nuno1212s.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON human editable item
 */
@SuppressWarnings("unchecked")
public class SerializableItem extends ItemStack {

    public SerializableItem(JSONObject jsonObject) {
        super();

        try {
            setType(Material.valueOf((String) jsonObject.get("Material")));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            setType(Material.STONE);
        }

        if (jsonObject.containsKey("Amount")) {
            setAmount(((Long) jsonObject.get("Amount")).intValue());
        } else {
            setAmount(1);
        }

        if (jsonObject.containsKey("Data")) {
            setDurability(((Long) jsonObject.get("Data")).shortValue());
        } else {
            setDurability((short) 0);
        }

        ItemMeta m = getItemMeta();

        if (jsonObject.containsKey("DisplayName")) {
            m.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String) jsonObject.get("DisplayName")));
        }

        if (jsonObject.containsKey("Lore")) {
            JSONArray lore = (JSONArray) jsonObject.get("Lore");
            List<String> newLore = new ArrayList<>();

            lore.forEach((loreLine) -> {
                newLore.add(ChatColor.translateAlternateColorCodes('&', (String) loreLine));
            });

            m.setLore(newLore);
        }

        if (jsonObject.containsKey("Enchantments")) {
            JSONArray enchantments = (JSONArray) jsonObject.get("Enchantments");
            enchantments.forEach((enchantment) -> {
                String[] split = ((String) enchantment).split(":");
                Enchantment enc = getEnchantment(split[0].toUpperCase());
                int level = Integer.parseInt(split[1]);
                m.addEnchant(enc, level ,true);
            });
        }

        if (jsonObject.containsKey("ItemFlags")) {
            JSONArray itemFlags = (JSONArray) jsonObject.get("ItemFlags");
            itemFlags.forEach((itemFlag) -> {
                ItemFlag flag = ItemFlag.valueOf(((String) itemFlag).toUpperCase());
                m.addItemFlags(flag);
            });
        }

        setItemMeta(m);

        if (jsonObject.containsKey("NBTData")) {
            // TODO: 25/10/2017 Add NBTData support
        }
    }

    public static Enchantment getEnchantment(String enchantment) {
        switch (enchantment) {
            case "POWER": {
                return Enchantment.ARROW_DAMAGE;
            }
            case "FLAME": {
                return Enchantment.ARROW_FIRE;
            }
            case "PUNCH": {
                return Enchantment.ARROW_KNOCKBACK;
            }
            case "INFINITY": {
                return Enchantment.ARROW_INFINITE;
            }
            case "SHARPNESS": {
                return Enchantment.DAMAGE_ALL;
            }
            case "BANE_OF_ARTHROPODS": {
                return Enchantment.DAMAGE_ARTHROPODS;
            }
            case "SMITE": {
                return Enchantment.DAMAGE_UNDEAD;
            }
            case "DEPTH_STRIDER": {
                return Enchantment.DEPTH_STRIDER;
            }
            case "EFFICIENCY": {
                return Enchantment.DIG_SPEED;
            }
            case "UNBREAKING": {
                return Enchantment.DURABILITY;
            }
            case "FIRE_ASPECT": {
                return Enchantment.FIRE_ASPECT;
            }
            case "KNOCKBACK": {
                return Enchantment.KNOCKBACK;
            }
            case "FORTUNE": {
                return Enchantment.LOOT_BONUS_BLOCKS;
            }
            case "LOOTING": {
                return Enchantment.LOOT_BONUS_MOBS;
            }
            case "LUCK": {
                return Enchantment.LUCK;
            }
            case "LURE": {
                return Enchantment.LURE;
            }
            case "PROTECTION": {
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            }
            case "BLAST_PROTECTION": {
                return Enchantment.PROTECTION_EXPLOSIONS;
            }
            case "FEATHER_FALLING": {
                return Enchantment.PROTECTION_FALL;
            }
            case "FIRE_PROTECTION": {
                return Enchantment.PROTECTION_FIRE;
            }
            case "PROJECTILE_PROTECTION": {
                return Enchantment.PROTECTION_PROJECTILE;
            }
            case "SILK_TOUCH": {
                return Enchantment.SILK_TOUCH;
            }
            case "THORNS": {
                return Enchantment.THORNS;
            }
            case "WATER_WORKER": {
                return Enchantment.WATER_WORKER;
            }
            default: {
                return null;
            }
        }
    }

}
