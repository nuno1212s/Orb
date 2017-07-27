package com.nuno1212s.classes.inventories;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.TimeUtil;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KInventoryItem extends InventoryItem {

    @Getter
    int kitID;

    public KInventoryItem(JSONObject j) {
        super(j);

        if (j.containsKey("KitID")) {
            this.kitID = ((Long) j.get("KitID")).intValue();
        } else {
            this.kitID = -1;
        }
    }

    public boolean isKit() {
        return kitID != -1;
    }

    public Kit getKit() {
        return Main.getIns().getKitManager().getKit(kitID);
    }

    public ItemStack buildItem(Player p) {
        Kit k = Main.getIns().getKitManager().getKit(kitID);

        if (k == null) {
            return super.getItem();
        }

        ItemStack displayItem = this.getItem().clone();

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());
        KitPlayer kitPlayer;

        Map<String, String> placeHolders = new HashMap<>();

        if (playerData instanceof KitPlayer) {
            kitPlayer = (KitPlayer) playerData;
            placeHolders.put("%time%", new TimeUtil("DD dias: HH horas: MM minutos: SS segundos")
                    .toTime(kitPlayer.timeUntilUsage(k.getId(), k.getDelay())));
        }

        if (!k.canUseKit(p, playerData)) {
            placeHolders.put("%canUse%", MainData.getIns().getMessageManager().getMessage("KIT_NO_PERMISSION").toString());
        } else {
            placeHolders.put("%canUse%", MainData.getIns().getMessageManager().getMessage("KIT_CAN_USE").toString());
        }

        displayItem = ItemUtils.formatItem(displayItem, placeHolders);

        return displayItem;
    }

}
