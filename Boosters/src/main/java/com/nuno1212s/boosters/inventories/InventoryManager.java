package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
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
     * Builds the default booster inventory for a player
     *
     * @param player
     * @param page   The page number (Starts at 1)
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

    /**
     * Get the current page of a player
     *
     * @param player The player to get
     * @return
     */
    public int getPage(UUID player) {
        return this.pages.getOrDefault(player, 1);
    }

    /**
     * Set the current page of a player
     *
     * @param player The player
     * @param page The current page
     */
    public void setPage(UUID player, int page) {
        this.pages.put(player, page);
    }

    /**
     * Remove the current player page from storage
     * @param player
     */
    public void removePage(UUID player) {
        this.pages.remove(player);
    }

    /**
     * Get the boosters for a specific page
     *
     * @param player The player to get the boosters from
     * @param page The page number (1 -> n)
     * @param perPage The amount of boosters per page
     * @return
     */
    public List<Booster> getBoosterForPage(UUID player, int page, int perPage) {
        List<Booster> boosterForPlayer = Main.getIns().getBoosterManager().getBoostersForPlayer(player);

        boosterForPlayer.sort(new Comparator<Booster>() {
            @Override
            public int compare(Booster o1, Booster o2) {
                return ((Boolean) o1.isActivated()).compareTo(o2.isActivated());
            }
        });

        if (boosterForPlayer.size() < perPage * (page - 1)) {
            return new ArrayList<>();
        } else if (boosterForPlayer.size() > perPage * page) {
            return boosterForPlayer.subList(perPage * (page - 1), perPage * page);
        } else {
            return boosterForPlayer;
        }
    }

    /**
     * Build the activation confirm inventory for a given booster
     *
     * @param b The booster
     * @return
     */
    public Inventory buildConfirmInventory(Booster b) {
        Inventory i = this.confirmInventory.buildInventory();

        InventoryItem booster = this.confirmInventory.getItemWithFlag("BOOSTER");

        i.setItem(booster.getSlot(), formatItem(b));

        return i;
    }

    /**
     * Format an item with the booster information given
     *
     * @param b Booster data
     * @return
     */
    public ItemStack formatItem(Booster b) {
        ItemStack boosterItem = this.boosterItem.clone();
        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("%booster%", b.getCustomName());
        placeHolders.put("%multiplier%", String.format("%.2f", b.getMultiplier()));
        placeHolders.put("%duration%", String.valueOf(TimeUnit.MILLISECONDS.toHours(b.getDurationInMillis())));
        placeHolders.put("%activated%", b.isActivated() ?
                MainData.getIns().getMessageManager().getMessage("BOOSTER_ACTIVATED").toString()
                : MainData.getIns().getMessageManager().getMessage("BOOSTER_DEACTIVATED").toString());
        boosterItem = ItemUtils.formatItem(boosterItem, placeHolders);
        NBTCompound nbt = new NBTCompound(boosterItem);
        nbt.add("BoosterID", b.getBoosterID());
        return nbt.write(boosterItem);
    }

    /**
     * Get the booster that an item represents
     *
     * @param i The item to check
     * @return The booster the item represents
     */
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
