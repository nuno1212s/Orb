package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.BoosterData;
import com.nuno1212s.boosters.boosters.BoosterType;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Inventory item class for the booster store, stores the booster information
 */
@SuppressWarnings("unchecked")
@Getter
public class BInventoryItem extends InventoryItem {

    BoosterData data;

    public BInventoryItem(JSONObject ob) {
        super(ob);
        float multiplier = (Float) ob.getOrDefault("Multiplier", 1f);

        int quantity = ((Long) ob.getOrDefault("Quantity", 1)).intValue();

        long price = (Long) ob.getOrDefault("Price", 1000);

        long durationInMillis = TimeUnit.MINUTES.toMillis(
                (Long) ob.getOrDefault("Duration", 60));

        String customName = ChatColor.translateAlternateColorCodes('&',
                (String) ob.getOrDefault("CustomName", "Default booster"));

        BoosterType type = BoosterType.valueOf(
                ((String) ob.getOrDefault("Type", "PLAYER_SERVER")).toUpperCase());

        String applicableServer = (String) ob.getOrDefault("ApplicableServer",
                MainData.getIns().getServerManager().getServerType());

        boolean cash = (Boolean) ob.getOrDefault("IsCash", true);

        data = new BoosterData(multiplier, quantity, price, durationInMillis, customName, type, applicableServer, cash);
    }


}
