package com.nuno1212s.classes.inventories;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KInventoryData extends InventoryData<KInventoryItem> {


    public KInventoryData(File jsonFile) {
        super(jsonFile, KInventoryItem.class, true);
    }

    public Inventory buildInventory(Player p) {
        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (InventoryItem item : this.items) {
            if (item instanceof KInventoryItem) {
                i.setItem(item.getSlot(), ((KInventoryItem) item).buildItem(p));
            } else {
                i.setItem(item.getSlot(), item.getItem().clone());
            }
        }

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }


        KInventoryItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (item.isKit()) {

            Kit kit = item.getKit();

            if (e.getClick().isLeftClick()) {

                kit.giveKitTo((Player) e.getWhoClicked());
                e.getClickedInventory().setContents(buildInventory((Player) e.getWhoClicked()).getContents());

            } else if (e.getClick().isRightClick()) {

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(kit.getClassItems());

            }
        }

    }

}

class KInventoryItem extends InventoryItem {

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

    /**
     * Build the item to get displayed in the kit inventory
     *
     * @param p
     * @return
     */
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

            if (kitPlayer.timeUntilUsage(k.getId(), k.getDelay()) > 0) {
                placeHolders.put("%time%", new TimeUtil("DD dias: HH horas: MM minutos: SS segundos")
                        .toTime(kitPlayer.timeUntilUsage(k.getId(), k.getDelay())));
            } else {
                placeHolders.put("%KIT_CAN_USE%", MainData.getIns().getMessageManager().getMessage("KIT_CAN_USE").toString());
            }
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
