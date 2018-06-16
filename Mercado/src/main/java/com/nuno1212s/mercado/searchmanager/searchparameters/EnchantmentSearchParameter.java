package com.nuno1212s.mercado.searchmanager.searchparameters;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.mercado.util.RomanNumber;
import com.nuno1212s.util.SerializableItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles enchantments search parameters
 */
public class EnchantmentSearchParameter extends SearchParameter {

    private Enchantment enchant;

    private int level;

    public EnchantmentSearchParameter(String name, String param) {
        super(name);

        String[] split = param.split(":");
        if (split.length == 1) {
            enchant = SerializableItem.getEnchantment(split[0]);
            level = -1;
        } else {
            enchant = SerializableItem.getEnchantment(split[0]);
            level = Integer.parseInt(split[1]);
        }

    }

    @Override
    public boolean fitsSearch(Item item) {
        ItemStack item1 = item.getItem();

        if (item1.getEnchantments().containsKey(this.enchant)) {
            return level == -1 || item1.getEnchantments().get(this.enchant) == this.level;
        }

        return false;
    }

    @Override
    public ItemStack formatItem(JSONObject item) {

        JSONObject object = new JSONObject(item);

        if (item.containsKey("DisplayName")) {

            String displayName = (String) item.get("DisplayName");

            if (level > 0)
                object.put("DisplayName", displayName.replace("%level%", RomanNumber.toRoman(level)));

        } else if (item.containsKey("Lore")) {

            List<String> lore = (JSONArray) item.get("Lore");

            List<String> newLore = new ArrayList<>();

            for (String s : lore) {
                if (level > 0)
                    newLore.add(s.replace("%level%", RomanNumber.toRoman(level)));
            }

            object.put("Lore", newLore);

        }

        return new SerializableItem(object);
    }

    @Override
    public SearchParameters getParameterType() {
        return SearchParameters.ENCHANT;
    }
}
