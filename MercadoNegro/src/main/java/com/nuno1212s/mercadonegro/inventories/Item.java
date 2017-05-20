package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.economy.ServerEconomyHandler;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An inventory item
 */
@AllArgsConstructor
@Getter
public class Item {

    ItemStack item;

    long cost;

    boolean isServerCurrency;

    public Item(Map<String, Object> jsonData) {
        try {
            this.item = ItemUtils.itemFrom64((String) jsonData.get("Item"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cost = ((Long) jsonData.get("Cost"));
        this.isServerCurrency = ((Boolean) jsonData.get("IsServerCurrency"));
    }

    public ItemStack buildDisplayItem() {
        ItemStack clone = item.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add("");
        Message cost = this.isServerCurrency ? MainData.getIns().getMessageManager().getMessage("COST_COINS")
                : MainData.getIns().getMessageManager().getMessage("COST_CASH");
        lore.add(cost.toString());
        itemMeta.setLore(lore);
        clone.setItemMeta(itemMeta);
        return clone;
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public void buy(Player p, PlayerData playerData) {
        if (isServerCurrency()) {
            ServerEconomyHandler economyHandler = Main.getIns().getEconomyHandler();
            if (economyHandler == null) {
                MainData.getIns().getMessageManager().getMessage("NO_SUPPORT").sendTo(p);
                return;
            }
            if (economyHandler.charge(playerData.getPlayerID(), getCost())) {
                MainData.getIns().getMessageManager().getMessage("BOUGHT_ITEM_SERVER_CURRENCY")
                        .format("%price%", String.valueOf(getCost()))
                        .sendTo(p);
                p.getInventory().addItem(getItem().clone());
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_SERVER_CURRENCY")
                        .sendTo(p);
            }
        } else {
            if (playerData.getCash() > getCost()) {
                playerData.setCash(playerData.getCash() - getCost());
                MainData.getIns().getMessageManager().getMessage("BOUGHT_ITEM_CASH")
                        .format("%price%", String.valueOf(getCost()))
                        .sendTo(p);
                p.getInventory().addItem(getItem());
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_CASH")
                        .sendTo(p);
            }
        }
    }

    public Map<String, Object> toJSONData() {
        Map<String, Object> data = new HashMap<>();
        data.put("Item", ItemUtils.itemTo64(this.item));
        data.put("Cost", this.cost);
        data.put("IsServerCurrency", this.isServerCurrency);
        return data;
    }

}
