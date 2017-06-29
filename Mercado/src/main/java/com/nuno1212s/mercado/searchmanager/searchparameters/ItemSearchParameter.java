package com.nuno1212s.mercado.searchmanager.searchparameters;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.util.SerializableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Item search parameter
 */
public class ItemSearchParameter extends SearchParameter {

    private Material material;

    public ItemSearchParameter(String name, String param) {
        super(name);
        material = Material.getMaterial(param);
    }

    @Override
    public boolean fitsSearch(Item item) {
        return item.getItem().getType() == this.material;
    }

    @Override
    public ItemStack formatItem(JSONObject item) {
        Map<String, Object> itemData = new HashMap<>(item);
        String material = ((String) item.get("Material")).replace("%material%", this.material.name());
        item.put("Material", material);
        return new SerializableItem(new JSONObject(itemData));
    }
}
