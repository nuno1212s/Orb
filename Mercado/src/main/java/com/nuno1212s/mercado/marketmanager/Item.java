package com.nuno1212s.mercado.marketmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Item data class
 */
@AllArgsConstructor
@Getter
public class Item {

    private String itemID;

    private UUID owner;

    private ItemStack item;

    private long cost, placeTime, soldTime;

    private boolean serverCurrency, sold;

    public Item(String itemID, UUID owner, String item, long cost, long placeTime, long soldTime, boolean serverCurrency, boolean sold) {
        this.itemID = itemID;
        this.owner = owner;
        try {
            this.item = ItemUtils.itemFrom64(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cost = cost;
        this.placeTime = placeTime;
        this.soldTime = soldTime;
        this.serverCurrency = serverCurrency;
        this.sold = sold;
    }

    public ItemStack getDisplayItem() {
        ItemStack clone = item.clone();
        ItemMeta itemMeta = clone.getItemMeta();

        List<String> lore;

        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        //TODO: Formatting

        NBTCompound itemData = new NBTCompound(clone);

        itemData.add("ItemID", this.getItemID());
        itemData.add("MarketItem", true);

        return itemData.write(clone);
    }



    @Override
    public boolean equals(Object obj) {
        return obj instanceof Item && ((Item) obj).getItemID().equals(this.getItemID());
    }
}
