package com.nuno1212s.crates.crates;

import com.nuno1212s.crates.Reward;
import com.nuno1212s.crates.animations.Animation;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.ServerCurrencyHandler;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * The crate information
 */
@Getter
@NoArgsConstructor
public class Crate {

    private String crateName, displayName;

    private Set<Reward> rewards;

    @Setter
    private boolean cash;

    @Setter
    private long keyCost;

    public Crate(String crateName, String displayName, Set<Reward> rewards, boolean cash, long keyCost) {
        this.crateName = crateName;
        this.displayName = displayName;
        this.rewards = rewards;
        this.cash = cash;
        this.keyCost = keyCost;


        recalculateProbabilities();
    }

    /**
     * Recalculate the probabilities with the given rewards
     */
    public void recalculateProbabilities() {
        if (this.rewards.isEmpty()) {
            return;
        }
        int currentFullProbability = 0;
        for (Reward reward : this.rewards) {
            currentFullProbability += (int) reward.getOriginalProbability();
        }

        double multiplier = 0;

        /*
        currentFullProbability = 1
        100 = multiplier
         */

        if (currentFullProbability != 100) {
            multiplier = (100D / currentFullProbability);
        }

        for (Reward reward : this.rewards) {
            reward.recalculateProbability(multiplier);
        }
    }

    /**
     * Remove a reward from the crate
     *
     * @param rewardID The ID of the reward to be removed
     * @return
     */
    public boolean removeReward(int rewardID) {
        return this.rewards.removeIf(r -> r.getRewardID() == rewardID);
    }

    /**
     * Get the next applicable reward ID
     *
     * Avoids repeated reward IDs
     *
     * @return
     */
    public int getNextRewardID() {
        int maxID = -1;
        for (Reward reward : this.rewards) {
            int rewardID = reward.getRewardID();
            if (rewardID > maxID) {
                maxID = rewardID;
            }
        }
        return ++maxID;
    }

    /**
     * Open a case for the player
     *
     * @param p The player to open the case to
     */
    public void openCase(Player p) {

        Animation animation = Main.getIns().getCrateManager().getAnimationManager().getRandomAnimation(this, p);

        Main.getIns().getCrateManager().getAnimationManager().registerAnimation(animation);

        p.openInventory(animation.getToEdit());
    }

    public ItemStack getRandomReward() {
        Random r = new Random();

        double v = r.nextDouble() * 100, currently = 0;

        for (Reward reward : this.rewards) {
            if (v > currently && v <= (currently += reward.getProbability())) {
                return reward.getItem().clone();
            }
        }

        return null;
    }

    public ItemStack formatKeyItem() {
        ItemStack clone = Main.getIns().getCrateManager().getDefaultKeyItem().clone();
        ItemMeta itemMeta = clone.getItemMeta();

        if (itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%crateName%", getDisplayName()));
        }

        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore(), newLore = new ArrayList<>();

        lore.forEach(loreLine ->
            newLore.add(loreLine.replace("%crateName%", getDisplayName()))
        );

        itemMeta.setLore(newLore);
        clone.setItemMeta(itemMeta);

        NBTCompound compound = new NBTCompound(clone);
        compound.add("Crate", getCrateName());
        return compound.write(clone);
    }

    public void buyKey(Player p) {
        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        if (cash) {
            if (playerData.getCash() >= this.getKeyCost()) {
                playerData.setCash(playerData.getCash() - getKeyCost());
                p.getInventory().addItem(formatKeyItem());
                MainData.getIns().getMessageManager().getMessage("BOUGHT_KEY_CASH")
                        .format("%crateName%", getDisplayName())
                        .format("%price%", String.valueOf(getKeyCost()))
                        .sendTo(p);
            } else {
                //NO MONEY
                MainData.getIns().getMessageManager().getMessage("NO_CASH")
                        .sendTo(p);
            }
        } else {
            if (!MainData.getIns().hasServerCurrency()) {
                System.out.println("Server does not support coin purchases");
                return;
            }
            ServerCurrencyHandler serverEconomyInterface = MainData.getIns().getServerCurrencyHandler();

            if (serverEconomyInterface.removeCurrency(playerData, this.getKeyCost())) {
                p.getInventory().addItem(formatKeyItem());
                MainData.getIns().getMessageManager().getMessage("BOUGHT_KEY_COINS")
                        .format("%crateName%", getDisplayName())
                        .format("%price%", String.valueOf(getKeyCost()))
                        .sendTo(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_COINS")
                        .sendTo(p);
            }
        }

    }

    boolean checkIsKey(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        NBTCompound compound = new NBTCompound(item);

        Map<String, Object> values = compound.getValues();
        if (values.containsKey("Crate")) {
            if (((String) values.get("Crate")).equalsIgnoreCase(getCrateName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Build the crate display inventory
     */
    public Inventory buildCrateDisplay() {
        Inventory displayInventory = Main.getIns().getCrateManager().getCrateDisplayInventory().buildInventory();

        int starting = 10;
        for (Reward reward : this.rewards) {
            displayInventory.setItem(starting++, reward.getItem().clone());

            //Ignore last and first slot of the inventories
            if (starting + 1 % 9 == 0) {
                starting += 2;
            }

            if (starting > displayInventory.getSize()) {
                break;
            }
        }

        InventoryItem aReturn = Main.getIns().getCrateManager().getCrateDisplayInventory().getItemWithFlag("RETURN");
        if (aReturn == null) {
            return displayInventory;
        }

        ItemStack i = displayInventory.getItem(aReturn.getSlot());

        // TODO: 22/10/2017 Append this crates name to the return item NBT so we can return to this inventory later

        return displayInventory;
    }

    /**
     * Get the confirm inventory for buying a key
     * @return
     */
    public Inventory getBuyKeyConfirmInventory() {
        String costString = isCash() ?
                MainData.getIns().getMessageManager().getMessage("CRATE_BUY_CASH")
                        .format("%price%", String.valueOf(getKeyCost())).toString()
                : MainData.getIns().getMessageManager().getMessage("CRATE_BUY_COINS")
                .format("%price%", String.valueOf(getKeyCost())).toString();
        Map<String, String> costPlaceHolder = new Pair<String, String>("%cost%", costString).toMap();

        InventoryData confirmInventory = Main.getIns().getCrateManager().getConfirmInventory();
        Inventory inventory = confirmInventory.buildInventory(costPlaceHolder);

        InventoryItem show_items = confirmInventory.getItemWithFlag("SHOW_ITEM");
        inventory.setItem(show_items.getSlot(), formatKeyItem());

        return inventory;
    }

}
