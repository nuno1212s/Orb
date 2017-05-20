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
        NBTCompound nbtCompound = new NBTCompound(clone);
        String price;
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        if (/*MarketManager.getIns().isTool(clone.getType())*/true) {
            String repairMessage = "", repairCashOnly;
            boolean cashItem;

            if (nbtCompound.getValues().containsKey("CashItem") ) {
                if (nbtCompound.getValues().get("CashItem") instanceof Long) {
                    cashItem = (long) (nbtCompound.getValues().get("CashItem")) == 1;
                } else {
                    cashItem = (int) (nbtCompound.getValues().get("CashItem")) == 1;
                }
                if (cashItem) {
                    repairCashOnly = MainData.getIns().getMessageManager().getMessage("MARKET_REPAIR_CASH").toString();
                } else {
                    repairCashOnly = MainData.getIns().getMessageManager().getMessage("MARKET_REPAIR_COINS").toString();
                }
            } else {
                cashItem = false;
                repairCashOnly = MainData.getIns().getMessageManager().getMessage("MARKET_REPAIR_COINS").toString();
            }

            if (nbtCompound.getValues().containsKey("RepairTimes")) {
                int repairTimes;
                if (nbtCompound.getValues().get("RepairTimes") instanceof Long) {
                    repairTimes = ((Long) nbtCompound.getValues().get("RepairTimes")).intValue();
                } else {
                    repairTimes = (int) nbtCompound.getValues().get("RepairTimes");
                }
                /*repairMessage = MainData.getIns().getMessageManager().getMessage("MARKET_ITEM_REPAIRED").format(
                        new AbstractMap.SimpleEntry<String, String>("%repairTimes%", String.valueOf(repairTimes)),
                        new AbstractMap.SimpleEntry<String, String>("%nextCost%", String.valueOf(cashItem ? RepairItemsCost.cashCost(repairTimes) : RepairItemsCost.coinCost(repairTimes))),
                        new AbstractMap.SimpleEntry<String, String>("%coin%", (cashItem ? "cash" : "coins")));*/
            } else {
                repairMessage = MainData.getIns().getMessageManager().getMessage("MARKET_ITEM_NEW").toString();
            }
            lore.add("");
            lore.add(repairMessage);
            lore.add("");
            lore.add(repairCashOnly);
        }
        price = MainData.getIns().getMessageManager().getMessage("MARKET_PRICE"  + (!this.serverCurrency ? "_CASH": ""))
                .format("%price%", String.valueOf(this.getCost())).toString();
        lore.add("");
        lore.add(price);
        itemMeta.setLore(lore);
        clone.setItemMeta(itemMeta);
        nbtCompound.add("MarketId", this.itemID);
        nbtCompound.add("MarketItem", 1);
        return nbtCompound.write(clone);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Item && ((Item) obj).getItemID().equals(this.getItemID());
    }
}
