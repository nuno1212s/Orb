package com.nuno1212s.rewards;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.ServerCurrencyHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Rewards
 */
public class Reward {

    @Getter
    private int id;

    @Getter
    private RewardType type;

    @Getter
    private String serverType;

    @Getter
    private ItemStack item;

    @Getter
    private boolean isDefault;

    @Setter
    private Object reward;

    public Reward(int id, RewardType type, boolean isDefault, String serverType, ItemStack item, Object reward) {
        this.id = id;
        this.type = type;
        this.isDefault = isDefault;
        this.serverType = serverType;
        this.item = item;
        this.reward = reward;
    }

    public Reward(int id, RewardType type, boolean isDefault, String serverType, String reward) {
        this.id = id;
        this.type = type;
        this.isDefault = isDefault;
        this.serverType = serverType;
        String[] split = reward.split(":");
        try {
            this.item = ItemUtils.itemFrom64(split[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (type) {
            case MESSAGE: {
                String[] messages = split[1].split(",");
                this.reward = Arrays.asList(messages);
                break;
            }

            case ITEM: {
                List<ItemStack> items = new ArrayList<>();
                String[] itemStrings = split[1].split(",");

                for (String item : itemStrings) {
                    try {
                        items.add(ItemUtils.itemFrom64(item));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                this.reward = items;
                break;
            }

            case SV_CRRCY: {
                this.reward = Long.valueOf(split[1]);
                break;
            }

            case CASH: {
                this.reward = Long.valueOf(split[1]);
                break;
            }
        }

    }

    public void deliver(Player p, PlayerData d) {

        if (!MainData.getIns().getServerManager().isApplicable(this.getServerType())) {
            MainData.getIns().getMessageManager().getMessage("NOT_APPLICABLE_HERE").sendTo(p);
            return;
        }

        if (d.hasClaimed(this.getId())) {
            MainData.getIns().getMessageManager().getMessage("ALREADY_CLAIMED").sendTo(p);
            return;
        }

        d.claim(this.getId());
        switch (type) {
            case MESSAGE: {
                List<String> messages = (List<String>) reward;

                messages.forEach(p::sendMessage);
                break;
            }

            case ITEM: {
                List<ItemStack> items = (List<ItemStack>) reward;

                PlayerInventory inventory = p.getInventory();

                World world = p.getWorld();

                Location location = p.getLocation();

                items.forEach((item) -> {
                    inventory.addItem(item.clone()).forEach((in, dropItem) -> {
                        world.dropItemNaturally(location, dropItem);
                    });
                });

                break;
            }

            case CASH: {
                long cash = (long) reward;

                d.setCash(d.getCash() + cash);
                MainData.getIns().getMessageManager().getMessage("INBOX_CASH")
                        .format("%cash%", String.valueOf(cash)).sendTo(p);

                break;
            }

            case SV_CRRCY: {
                long coins = (long) reward;

                ServerCurrencyHandler sCH = MainData.getIns().getServerCurrencyHandler();
                if (sCH != null) {
                    sCH.addCurrency(d, coins);
                    MainData.getIns().getMessageManager().getMessage("INBOX_CURRENCY")
                            .format("%coins%", String.valueOf(coins)).sendTo(p);
                }

                break;
            }

        }

    }

    public String rewardToString() {
        StringBuilder builder = new StringBuilder("");

        builder.append(ItemUtils.itemTo64(item));
        builder.append(":");

        switch (type) {
            case MESSAGE: {
                List<String> messages = (List<String>) reward;
                messages.forEach((message) -> {
                    builder.append(message);
                    builder.append(",");
                });

                break;
            }
            case ITEM: {
                List<ItemStack> items = (List<ItemStack>) reward;

                items.forEach((item) -> {
                    builder.append(ItemUtils.itemTo64(item));
                    builder.append(",");
                });

                break;
            }
            case CASH: {
                long cash = (long) reward;
                builder.append(cash);

                break;
            }
            case SV_CRRCY: {
                long coins = (long) reward;
                builder.append(coins);

                break;
            }

            default:
                break;
        }

        return builder.toString();
    }

    public static enum RewardType {

        MESSAGE,
        ITEM,
        CASH,
        SV_CRRCY;

    }

}
