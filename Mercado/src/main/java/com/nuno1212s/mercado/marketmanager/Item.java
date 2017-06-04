package com.nuno1212s.mercado.marketmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
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

    private UUID owner, buyer;

    private ItemStack item;

    @Setter
    private long cost, placeTime, soldTime;

    private boolean serverCurrency, sold;

    public Item(String itemID, UUID owner, UUID buyer, String item, long cost, long placeTime, long soldTime, boolean serverCurrency, boolean sold) {
        this.itemID = itemID;
        this.owner = owner;
        this.buyer = buyer;
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

    public void deliverItem(Player player) {
        this.sold = true;
        this.soldTime = System.currentTimeMillis();
        this.buyer = player.getUniqueId();
        player.getInventory().addItem(this.getItem());

        Pair<PlayerData, Boolean> playerData = MainData.getIns().getPlayerManager().getOrLoadPlayer(player.getUniqueId());
        if (playerData.getValue()) {
            PlayerData playerD = playerData.getKey();
            if (isServerCurrency()) {
                if (MainData.getIns().hasServerCurrency()) {
                    MainData.getIns().getServerCurrencyHandler().addCurrency(playerD, getCost());
                }
            } else {
                playerD.setCash(playerD.getCash() + getCost());
            }
        } else {
            //Player is offline or in another server
            //if the player is in another server, we must update that server as well
            PlayerData playerD = playerData.getKey();

            playerD.setCash(playerD.getCash() + getCost());
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Item && ((Item) obj).getItemID().equals(this.getItemID());
    }
}
