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

    private String match;

    public ItemSearchParameter(String name, String param) {
        super(name);

        if (param.startsWith("MATCH")) {
            match = param.split(":")[1];

            for (Material material1 : Material.values()) {
                if (material1.name().contains(match)) {
                    material = material1;
                    break;
                }
            }

            if (material == null) {
                material = Material.AIR;
            }
        } else {
            material = Material.getMaterial(param);
        }

    }

    @Override
    public boolean fitsSearch(Item item) {
        if (match != null) {
            System.out.println(match);
            return item.getItem().getType().name().contains(match);
        }
        System.out.println("eksde");
        return item.getItem().getType() == this.material;
    }

    @Override
    public ItemStack formatItem(JSONObject item) {
        Map<String, Object> itemData = new HashMap<>(item);
        String material = ((String) item.get("Material")).replace("%material%", this.material.name());
        itemData.put("Material", material);
        return new SerializableItem(new JSONObject(itemData));
    }

    @Override
    public SearchParameters getParameterType() {
        return SearchParameters.ITEM;
    }
}
