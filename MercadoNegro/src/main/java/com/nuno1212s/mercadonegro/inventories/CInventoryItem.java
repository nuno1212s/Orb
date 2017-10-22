package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.ServerCurrencyHandler;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nuno1212s.mercadonegro.inventories.CInventoryItem.RewardType.COMMAND;
import static com.nuno1212s.mercadonegro.inventories.CInventoryItem.RewardType.ITEM;

public class CInventoryItem extends InventoryItem {

    @Getter
    private RewardType type;

    @Getter
    private Object reward;

    @Getter
    private boolean isServerCurrency;

    @Getter
    private int cost;

    public CInventoryItem(JSONObject itemData) {
        super(itemData);

        this.cost = ((Long) itemData.getOrDefault("Cost", 0L)).intValue();
        this.isServerCurrency = (Boolean) itemData.getOrDefault("IsServerCurrency", false);
        this.type = RewardType.valueOf((String) itemData.getOrDefault("RewardType", "COMMAND"));
        this.reward = this.type.fromJSON(itemData);

    }

    /**
     * Get the display item
     *
     * @return The display item
     */
    public ItemStack getDisplayItem() {
        ItemStack item = getItem();

        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add("");
        Message cost = this.isServerCurrency ? MainData.getIns().getMessageManager().getMessage("COST_COINS")
                : MainData.getIns().getMessageManager().getMessage("COST_CASH");
        cost.format("%price", String.valueOf(this.getCost()));
        lore.add(cost.toString());
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;

    }

    /**
     * Buy the item from the player
     */
    public void buyItem(PlayerData playerData) {
        Player p = playerData.getPlayerReference(Player.class);

        if (isServerCurrency()) {
            ServerCurrencyHandler economyHandler = MainData.getIns().getServerCurrencyHandler();

            if (economyHandler == null) {
                MainData.getIns().getMessageManager().getMessage("NO_SUPPORT").sendTo(p);
                return;
            }

            if (economyHandler.removeCurrency(playerData, getCost())) {
                MainData.getIns().getMessageManager().getMessage("BOUGHT_ITEM_SERVER_CURRENCY")
                        .format("%price%", String.valueOf(getCost()))
                        .sendTo(p);
                giveItem(p);
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
                giveItem(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_CASH")
                        .sendTo(p);
            }
        }

    }

    /**
     * Give the reward to a player
     */
    private void giveItem(Player p) {
        if (getType() == ITEM) {
            p.getInventory().addItem((ItemStack) getReward());
        } else if (getType() == COMMAND) {
            String command = (String) getReward();

            command = command.replace("%player%", p.getName());

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public enum RewardType {
        COMMAND,
        ITEM;

        public Object fromJSON(JSONObject itemData) {
            if (this == COMMAND) {
                return (String) itemData.getOrDefault("Reward", "");
            } else if (this == ITEM) {
                return new SerializableItem((JSONObject) itemData.getOrDefault("Reward", new JSONObject()));
            }

            return null;
        }
    }

}
