package com.nuno1212s.rankup.crates;

import com.nuno1212s.rankup.crates.animations.Animation;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * The crate information
 */
@Getter
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

        System.out.println(rewards);
    }

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

    public boolean removeReward(int rewardID) {
        return this.rewards.removeIf(r -> r.getRewardID() == rewardID);
    }

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

    public void openCase(Player p) {

        Animation animation = Main.getIns().getCrateManager().getAnimationManager().getRandomAnimation(this);

        Main.getIns().getCrateManager().getAnimationManager().registerAnimation(animation);

        p.openInventory(animation.getToEdit());
    }

    public ItemStack getRandomReward() throws Exception {
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
        PVPPlayerData playerData = (PVPPlayerData) MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

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
            if (playerData.getCoins() >= this.getKeyCost()) {
                playerData.setCoins(playerData.getCoins() - getKeyCost());
                p.getInventory().addItem(formatKeyItem());
                MainData.getIns().getMessageManager().getMessage("BOUGHT_KEY_COINS")
                        .format("%crateName%", getDisplayName())
                        .format("%price%", String.valueOf(getKeyCost()))
                        .sendTo(p);
            } else {
                //NO MONEY
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

}
