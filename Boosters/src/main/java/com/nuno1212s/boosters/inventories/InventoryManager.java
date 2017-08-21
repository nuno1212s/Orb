package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Handles inventory management
 */
public class InventoryManager {

    @Getter
    private InventoryData mainInventory, confirmInventory;

    private ItemStack boosterItem;

    private Map<UUID, Integer> pages;

    public InventoryManager(Module m) {
        this.pages = new HashMap<>();
        this.mainInventory = new InventoryData(m.getFile("mainInventory.json", true));
        this.confirmInventory = new InventoryData(m.getFile("confirmInventory.json", true));
        File file = m.getFile("boosterItems.json", true);
        JSONObject boosterItem;
        try (Reader r = new FileReader(file)) {
            boosterItem = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }
        this.boosterItem = new SerializableItem(boosterItem);
    }

    /**
     *
     * @param player
     * @param page The page number (Starts at 1)
     * @return
     */
    public Inventory buildInventoryForPlayer(UUID player, int page) {
        Inventory inventory = mainInventory.buildInventory();
        int initialSlot = 0, finalSlot = inventory.getSize() - 18;
        List<Booster> boostersForPage = getBoosterForPage(player, page, finalSlot);

        for (; initialSlot < boostersForPage.size(); initialSlot++) {
            inventory.setItem(initialSlot, formatItem(boostersForPage.get(initialSlot)));
        }

        return inventory;
    }

    public int getPage(UUID player) {
        return this.pages.get(player);
    }

    public void setPage(UUID player, int page) {
        this.pages.put(player, page);
    }

    public void removePage(UUID player) {
        this.pages.remove(player);
    }

    public List<Booster> getBoosterForPage(UUID player, int page, int perPage) {
        List<Booster> boosterForPlayer = Main.getIns().getBoosterManager().getBoosterForPlayer(player);

        boosterForPlayer.sort(new Comparator<Booster>() {
            @Override
            public int compare(Booster o1, Booster o2) {
                return ((Boolean) o1.isActivated()).compareTo(o2.isActivated());
            }
        });

        return boosterForPlayer.subList(perPage * (page - 1), perPage * page);
    }

    public Inventory buildConfirmInventory(Booster b) {
        Inventory i = this.confirmInventory.buildInventory();

        InventoryItem booster = this.confirmInventory.getItemWithFlag("BOOSTER");

        i.setItem(booster.getSlot(), formatItem(b));

        return i;
    }

    public ItemStack formatItem(Booster b) {
        ItemStack boosterItem = this.boosterItem.clone();
        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("%booster%", b.getCustomName());
        placeHolders.put("%multipliers%", String.format("%.2f", b.getMultiplier()));
        placeHolders.put("%duration%", String.valueOf(TimeUnit.MILLISECONDS.toHours(b.getDurationInMillis())));
        boosterItem = ItemUtils.formatItem(boosterItem, placeHolders);
        NBTCompound nbt = new NBTCompound(boosterItem);
        nbt.add("BoosterID", b.getBoosterID());
        return nbt.write(boosterItem);
    }

    public Booster getBoosterConnectedToItem(ItemStack i) {
        Booster b = null;

        NBTCompound compound = new NBTCompound(i);
        Map<String, Object> values = compound.getValues();

        if (values.containsKey("BoosterID")) {
            String boosterID = (String) values.get("BoosterID");
            b = Main.getIns().getBoosterManager().getBooster(boosterID);
        }

        return b;
    }


}
