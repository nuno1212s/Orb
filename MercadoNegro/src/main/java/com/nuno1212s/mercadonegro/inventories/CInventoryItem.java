package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class CInventoryItem extends InventoryItem {

    @Getter
    private RewardType type;

    @Getter
    private Object reward;

    public CInventoryItem(JSONObject itemData) {
        super(itemData);

        this.type = RewardType.valueOf((String) itemData.getOrDefault("RewardType", "ITEM"));
        this.reward = this.type.fromJSON((JSONObject) itemData.get("Reward"));

    }

    public void deliverItem(PlayerData playerData) {
        Player player = playerData.getPlayerReference(Player.class);

        if (type == RewardType.ITEM) {
            player.getInventory().addItem((ItemStack) getReward());
        } else if (type == RewardType.COMMAND) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), (String) getReward());
        }

    }

    public enum RewardType {
        COMMAND,
        ITEM;

        public Object fromJSON(JSONObject itemData) {
            if (this == COMMAND) {
                return itemData.getOrDefault("Command", "");
            } else if (this == ITEM) {
                return new SerializableItem(itemData);
            }

            return null;
        }
    }

}
