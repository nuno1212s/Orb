package com.nuno1212s.hub.util;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory items
 */
public class HInventoryItem extends InventoryItem {

    @Getter
    private String connectingServer;

    public HInventoryItem(JSONObject json) {
        super(json);
        this.connectingServer = (String) json.getOrDefault("ConnectingServer", null);
    }

    public void sendPlayerToServer(Player p) {
        Main.getIns().getServerSelectorManager().sendPlayerToServer(p, this.connectingServer);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = super.getItem().clone();

        Pair<Integer, Integer> serverPlayerCount = MainData.getIns().getServerManager().getPlayerCount(connectingServer);
        String playerCount = String.valueOf(serverPlayerCount.key());
        String maxPlayerCount = String.valueOf(serverPlayerCount.value());

        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("%playerCount%", playerCount);
        placeHolders.put("%maxPlayers%", maxPlayerCount);

        return ItemUtils.formatItem(item, placeHolders);
    }
}
