package com.nuno1212s.inventories;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.ReflectionManager;
import com.nuno1212s.util.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Handles inventory listeners data
 */
@SuppressWarnings("unchecked")
@ToString
public class InventoryData<T extends InventoryItem> {

    @Getter
    protected String inventoryName;

    @Getter
    protected String inventoryID;

    @Getter
    protected int inventorySize;

    @Getter
    protected ImmutableList<T> items;

    protected boolean allowsMovement;

    @Getter(value = AccessLevel.PROTECTED)
    protected boolean directRedirect;

    /**
     * A function that get's called before the inventory is closed, as a preparation for the transfer of inventories
     */
    @Setter(value = AccessLevel.PROTECTED)
    private Callback<HumanEntity> onTransfer;

    /**
     * A function that get's called to open the next inventory, after the current inventory is closed
     *
     *
     */
    @Setter(value = AccessLevel.PROTECTED)
    private Callback<Pair<HumanEntity, InventoryData>> openFuction;

    /**
     * Use the {@link #InventoryData(File, Class, boolean)} instead, this constructor is just being kept for compatibility reasons
     *
     * @param jsonFile
     */
    @Deprecated
    public InventoryData(File jsonFile) {
        this(jsonFile, (Class<T>) InventoryItem.class, true);
    }

    public InventoryData(JSONObject obj) {
        this.directRedirect = true;

        load(obj, false,(Class<T>) InventoryItem.class);
    }

    public InventoryData(JSONObject obj, Class<T> itemClass) {
        this.directRedirect = true;

        load(obj, false, itemClass);
    }

    /**
     * Main constructor for the inventory data
     *
     * @param jsonFile  The file of the inventory
     * @param itemClass An optional class for the items stored in the inventory (If you want to change the
     *                  items that the inventory stores, there is no longer a need to create a subclass of InventoryData)
     */
    public InventoryData(File jsonFile, Class<T> itemClass, boolean directRedirect) {
        JSONObject jsOB;

        this.directRedirect = directRedirect;

        try (Reader r = new FileReader(jsonFile)) {
            jsOB = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            System.out.println("Failed to read the JSON File " + jsonFile.getName());
            return;
        }

        load(jsOB, true, itemClass == null ? (Class<T>) InventoryItem.class : itemClass);
    }

    public InventoryData(File jsonFile, Class<T> itemClass, boolean directRedirect, Callback<HumanEntity> transferFunction) {
        this(jsonFile, itemClass, directRedirect);

        this.onTransfer = transferFunction;
    }

    /**
     * Loads the actual inventory data from the JSONObject
     *
     * @param jsOB         The object to load from
     * @param loadItems    Should the items be loaded
     * @param customLoader The loader that should be used for the items
     */
    protected final void load(JSONObject jsOB, boolean loadItems, Class<T> customLoader) {
        this.inventoryName = ChatColor.translateAlternateColorCodes('&',
                (String) jsOB.getOrDefault("InventoryName", "&cFailed to load name"));

        this.inventoryID = (String) jsOB.getOrDefault("InventoryID", this.inventoryName);

        this.inventorySize = ((Long) jsOB.getOrDefault("InventorySize", 27L)).intValue();

        this.allowsMovement = (Boolean) jsOB.getOrDefault("AllowMovement", false);

        if (inventorySize % 9 != 0) {
            this.inventorySize = 27;
        }

        if (loadItems) {
            JSONArray inventoryItems = (JSONArray) jsOB.getOrDefault("InventoryItems", new JSONArray());

            ArrayList<T> items = new ArrayList<>(inventoryItems.size());
            Constructor constructor = ReflectionManager.getIns().getConstructor(customLoader, JSONObject.class);

            inventoryItems.forEach((inventoryItem) -> {
                        T e = (T) ReflectionManager.getIns()
                                .invokeConstructor(constructor, (JSONObject) inventoryItem);
                        items.add(e);
                    }
            );

            this.items = ImmutableList.copyOf(items);
        }

        MainData.getIns().getInventoryManager().registerInventory(this);
    }

    public Callback<HumanEntity> getTransferFunction() {
        return this.onTransfer;
    }

    /**
     * Get the function to open the connection inventory
     * @return
     */
    public Callback<Pair<HumanEntity, InventoryData>> getOpenFuction() {
        return openFuction;
    }

    /**
     * Build the inventory with the specified features
     *
     * @return
     */
    public Inventory buildInventory() {

        Inventory inventory = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        this.items.forEach(item -> {
            ItemStack item1 = item.getItem();
            if (item1 == null) {
                inventory.setItem(item.getSlot(), null);
                return;
            }
            inventory.setItem(item.getSlot(), item1.clone());
        });

        return inventory;
    }

    /**
     * Build the inventory and format items with the given placeHolders
     *
     * @param placeHolders
     * @return
     */
    public Inventory buildInventory(Map<String, String> placeHolders) {

        Inventory inventory = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        this.items.forEach(item -> {
            ItemStack item1 = item.getItem();
            if (item1 == null) {
                inventory.setItem(item.getSlot(), null);
                return;
            }

            inventory.setItem(item.getSlot(), ItemUtils.formatItem(item1.clone(), placeHolders));
        });

        return inventory;
    }

    public Inventory buildInventory(Player p) {
        return buildInventory();
    }

    public final T getItem(int slot) {
        for (T item : this.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public final T getItemWithFlag(String flag) {

        for (T item : this.items) {
            if (item.hasItemFlag(flag)) {
                return item;
            }
        }

        return null;
    }

    public final List<T> getItemsWithFlag(String flag) {

        List<T> items = new ArrayList<>();

        for (T item : this.items) {
            if (item.hasItemFlag(flag)) {
                items.add(item);
            }
        }

        return items;
    }

    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);
    }

    public final boolean equals(Inventory inv) {
        return inv.getSize() == this.getInventorySize()
                && inv.getName().equals(this.getInventoryName());
    }

    @Override
    public final boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj instanceof InventoryData) {
            //Compare the size before the name because the size is much faster to compare than the name
            // and the size, like the name can't be changed
            return ((InventoryData) obj).getInventorySize() == this.getInventorySize()
                    && ((InventoryData) obj).getInventoryName().equals(this.getInventoryName());
        }

        return false;
    }
}
