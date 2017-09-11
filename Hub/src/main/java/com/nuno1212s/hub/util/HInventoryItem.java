package com.nuno1212s.hub.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.BungeeSender;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

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
        PlayerData data = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        BungeeSender.getIns().sendPlayer(data, p, this.connectingServer);
    }

}
