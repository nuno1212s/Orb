package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.boosters.BoosterData;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.entity.Player;
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
    private InventoryData myBoostersInventory, confirmInventory, sellInventory, confirmSellInventory, landingInventory;

    private ItemStack boosterItem, buyBoosterItem;

    private Map<UUID, Integer> pages;

    public InventoryManager(Module m) {
        this.pages = new HashMap<>();

        this.myBoostersInventory = new InventoryData(m.getFile("myBoostersInventory.json", true), null);
        this.confirmInventory = new InventoryData(m.getFile("confirmInventory.json", true), null);
        this.confirmSellInventory = new InventoryData(m.getFile("confirmSellInventory.json", true), null);
        this.landingInventory = new InventoryData(m.getFile("landingInventory.json", true), null);
        this.sellInventory = new InventoryData(m.getFile("sellInventory.json", true), BInventoryItem.class);

        File file = m.getFile("boosterItems.json", true);

        JSONObject boosterItem;

        try (Reader r = new FileReader(file)) {
            boosterItem = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        this.boosterItem = new SerializableItem((JSONObject) boosterItem.get("NormalItem"));
        this.buyBoosterItem = new SerializableItem((JSONObject) boosterItem.get("BuyItem"));
    }

    /**
     * Builds the default booster inventory for a player
     *
     * @param player
     * @param page   The page number (Starts at 1)
     * @return
     */
    public Inventory buildInventoryForPlayer(UUID player, int page) {
        Inventory inventory = myBoostersInventory.buildInventory();
        int initialSlot = 0, finalSlot = inventory.getSize() - 18;
        List<Booster> boostersForPage = getBoosterForPage(player, page, finalSlot);

        for (; initialSlot < boostersForPage.size(); initialSlot++) {
            inventory.setItem(initialSlot, formatItem(boostersForPage.get(initialSlot)));
        }

        return inventory;
    }

    /**
     * Build the booster store inventory
     */
    public Inventory buildStoreInventory() {
        return this.sellInventory.buildInventory();
    }

    /**
     * Build the landing inventory
     */
    public Inventory buildLandingInventory() {
        return this.landingInventory.buildInventory();
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
     * @param page   The current page
     */
    public void setPage(UUID player, int page) {
        this.pages.put(player, page);
    }

    /**
     * Remove the current player page from storage
     *
     * @param player
     */
    public void removePage(UUID player) {
        this.pages.remove(player);
    }

    /**
     * Get the boosters for a specific page
     *
     * @param player  The player to get the boosters from
     * @param page    The page number (1 -> n)
     * @param perPage The amount of boosters per page
     * @return
     */
    public List<Booster> getBoosterForPage(UUID player, int page, int perPage) {
        List<Booster> boosterForPlayer = Main.getIns().getBoosterManager().getBoostersForPlayer(player);

        boosterForPlayer.sort(new Comparator<Booster>() {
            @Override
            public int compare(Booster o1, Booster o2) {
                return Boolean.compare(o1.isActivated(), o2.isActivated());
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

        if (booster == null) {
            return i;
        }

        i.setItem(booster.getSlot(), formatItem(b));

        return i;
    }

    /**
     * Build the confirm purchase inventory
     *
     * @param booster The booster item that is going to be purchased
     * @return
     */
    public Inventory buildBuyConfirmInventory(BInventoryItem booster) {
        Inventory inventory = this.confirmSellInventory.buildInventory();

        InventoryItem boosterItem = this.confirmSellInventory.getItemWithFlag("BOOSTER");

        if (boosterItem == null) {
            return inventory;
        }

        inventory.setItem(boosterItem.getSlot(), formatDisplayBooster(booster));

        return inventory;
    }

    /**
     * Handles the buying of the boosters (after the confirm buy inventory)
     *
     * @param owner The owner of the booster
     * @param item  The item with the booster information
     */
    public void buyBooster(Player owner, ItemStack item) {
        BoosterData boosterData = BoosterData.readFromItem(item);

        PlayerData d = MainData.getIns().getPlayerManager().getPlayer(owner.getUniqueId());

        if (boosterData.isCash()) {
            if (d.getCash() >= boosterData.getPrice()) {
                d.setCash(d.getCash() - boosterData.getPrice());
                List<Booster> boosters = Main.getIns().getBoosterManager().createBooster(owner.getUniqueId(), boosterData);

                MainData.getIns().getMessageManager().getMessage("BOUGHT_BOOSTER_CASH")
                        .format("%name%", boosterData.getCustomName())
                        .format("%price%", String.valueOf(boosterData.getPrice()))
                        .format("%quantity%", String.valueOf(boosterData.getQuantity())).sendTo(owner);

                for (Booster booster : boosters) {
                    Main.getIns().getBoosterManager().addBooster(booster);
                }

                MainData.getIns().getScheduler().runTaskAsync(() -> {
                    for (Booster booster : boosters) {
                        Main.getIns().getMysqlHandler().saveBooster(booster);
                    }
                });

            } else {
                MainData.getIns().getMessageManager().getMessage("NO_CASH").sendTo(owner);
            }
        } else {

            if (MainData.getIns().getServerCurrencyHandler().removeCurrency(d, boosterData.getPrice())) {
                //Need to call information update because the server currency handler does not do this automatically
                MainData.getIns().getEventCaller().callUpdateInformationEvent(d);
                List<Booster> boosters = Main.getIns().getBoosterManager().createBooster(owner.getUniqueId(), boosterData);

                MainData.getIns().getMessageManager().getMessage("BOUGHT_BOOSTER_COINS")
                        .format("%name%", boosterData.getCustomName())
                        .format("%price%", String.valueOf(boosterData.getPrice()))
                        .format("%quantity%", String.valueOf(boosterData.getQuantity())).sendTo(owner);

                for (Booster booster : boosters) {
                    Main.getIns().getBoosterManager().addBooster(booster);
                }

                MainData.getIns().getScheduler().runTaskAsync(() -> {
                    for (Booster booster : boosters) {
                        Main.getIns().getMysqlHandler().saveBooster(booster);
                    }
                });
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_COINS").sendTo(owner);
            }
        }

    }

    /**
     * Formats the booster item with the data from the item
     *
     * @param b The item
     * @return
     */
    private ItemStack formatDisplayBooster(BInventoryItem b) {
        ItemStack boosterItem = this.buyBoosterItem.clone();
        Map<String, String> placeHolders = new HashMap<>();

        BoosterData data = b.getData();
        placeHolders.put("%booster%", data.getCustomName());

        placeHolders.put("%multiplier%", String.format("%.2f", data.getMultiplier()));

        placeHolders.put("%duration%", String.valueOf(TimeUnit.MILLISECONDS.toHours(data.getDurationInMillis())));

        placeHolders.put("%price%", String.valueOf(data.getPrice()));

        boosterItem.setAmount(data.getQuantity());

        boosterItem = ItemUtils.formatItem(boosterItem, placeHolders);
        return data.writeToItem(boosterItem);
    }

    /**
     * Format an item with the booster information given
     *
     * @param b Booster data
     * @return
     */
    private ItemStack formatItem(Booster b) {
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
