package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.Booster;
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

    private float multiplier;

    private long durationInMillis;

    private String customName;

    private BoosterType type;

    private String applicableServer;

    public BInventoryItem(JSONObject ob) {
        super(ob);
        this.multiplier = (Float) ob.getOrDefault("Multiplier", 1f);

        this.durationInMillis = TimeUnit.MINUTES.toMillis(
                (Long) ob.getOrDefault("Duration", 60));

        this.customName = ChatColor.translateAlternateColorCodes('&',
                (String) ob.getOrDefault("CustomName", "Default booster"));

        this.type = BoosterType.valueOf(
                ( (String) ob.getOrDefault("Type", "PLAYER_SERVER") ).toUpperCase());

        this.applicableServer = (String) ob.getOrDefault("ApplicableServer",
                MainData.getIns().getServerManager().getServerType());
    }


}
