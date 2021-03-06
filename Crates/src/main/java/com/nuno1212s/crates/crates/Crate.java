package com.nuno1212s.crates.crates;

import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.animations.Animation;
import com.nuno1212s.economy.CurrencyHandler;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    transient Random r = new Random();

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

        double currentFullProbability = 0;

        for (Reward reward : this.rewards) {
            currentFullProbability += reward.getOriginalProbability();
        }

        /*
        currentFullProbability = 1
        100 = multiplier
         */
        double multiplier = (100D / currentFullProbability);

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
     * <p>
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

    /**
     * Get a random reward
     *
     * @return
     */
    public Reward getRandomReward() {
        double v = r.nextDouble() * 100, currently = 0;

        for (Reward reward : this.rewards) {
            if (v >= currently && v <= (currently += reward.getProbability())) {
                return reward;
            }
        }

        return null;
    }

    /**
     * Get the key item for the Crate
     * @return
     */
    public ItemStack formatKeyItem() {
        ItemStack clone = Main.getIns().getCrateManager().getDefaultKeyItem().clone();

        Map<String, String> formats = new HashMap<>();

        formats.put("%crateName%", this.getDisplayName());

        NBTCompound compound = new NBTCompound(clone);
        compound.add("Crate", getCrateName());

        return ItemUtils.formatItem(compound.write(clone), formats);
    }

    public void buyKey(Player p) {
        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        if (cash) {
            if (playerData.removeCash(getKeyCost())) {
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

            MainData.getIns().getServerCurrencyHandler().removeCurrency(playerData, this.getKeyCost())
                    .thenAccept((completed) -> {
                        if (completed) {
                            p.getInventory().addItem(formatKeyItem());
                            MainData.getIns().getMessageManager().getMessage("BOUGHT_KEY_COINS")
                                    .format("%crateName%", getDisplayName())
                                    .format("%price%", String.valueOf(getKeyCost()))
                                    .sendTo(p);
                        } else {
                            MainData.getIns().getMessageManager().getMessage("NO_COINS")
                                    .sendTo(p);
                        }
                    });
        }

    }

    /**
     * Check if a given item is a key
     *
     * @param item
     * @return
     */
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
            for (ItemStack item : reward.getItems()) {
                displayInventory.setItem(starting++, item);

                //Ignore last and first slot of the inventories
                if (starting + 1 % 9 == 0) {
                    starting += 2;
                }

                if (starting > displayInventory.getSize()) {
                    break;
                }
            }
        }

        InventoryItem aReturn = Main.getIns().getCrateManager().getCrateDisplayInventory().getItemWithFlag("RETURN");
        if (aReturn == null) {
            return displayInventory;
        }

        ItemStack i = displayInventory.getItem(aReturn.getSlot());

        NBTCompound nbt = new NBTCompound(i);

        nbt.add("Crate", this.getCrateName());

        displayInventory.setItem(aReturn.getSlot(), nbt.write(i));

        return displayInventory;
    }

    /**
     * Get the confirm inventory for buying a key
     *
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

    /**
     * Get the reward of the given ID
     *
     * @param rewardID The id of the reward
     * @return
     */
    public Reward getReward(int rewardID) {
        for (Reward reward : this.rewards) {
            if (reward.getRewardID() == rewardID) {
                return reward;
            }
        }

        return null;
    }

    /**
     * Get the reward by a display item
     *
     * @param item The item of the reward
     * @return
     */
    public Reward getReward(ItemStack item) {
        NBTCompound nbtCompound = new NBTCompound(item);
        if (nbtCompound.getValues().containsKey("RewardID")) {
            int rewardID = (Integer) nbtCompound.getValues().get("RewardID");

            return getReward(rewardID);

        }

        return null;
    }

}
