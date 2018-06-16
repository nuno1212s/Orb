package com.nuno1212s.sellsigns.signs;

import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.multipliers.multipliers.RankMultiplier;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.StringReader;

/**
 * Sell sign
 */
public class StoreSign {

    @Getter
    private int id;

    @Getter
    @Setter
    private ItemStack item;

    private Location l;

    @Getter
    private int price, sellPrice;

    @Getter
    private boolean canSell, canBuy;

    @Getter
    @Setter
    private String individualMultiplier;

    public StoreSign(JSONObject object) {
        this.id = ((Long) object.get("ID")).intValue();
        YamlConfiguration item = YamlConfiguration.loadConfiguration(new StringReader((String) object.get("Item")));
        this.item = item.getItemStack("Item");
        this.l = new SerializableLocation((JSONObject) object.get("Location"));
        this.price = ((Long) object.get("Price")).intValue();
        this.sellPrice = ((Long) object.get("SellPrice")).intValue();
        this.canSell = (Boolean) object.get("CanSell");
        this.canBuy = (Boolean) object.get("CanBuy");

        if (object.containsKey("IndividualMult")) {
            this.individualMultiplier = (String) object.get("IndividualMult");
        }
    }

    public StoreSign(int id, Location l, int price, int sellPrice, boolean canSell, boolean canBuy) {
        this.id = id;
        this.l = l;
        this.price = price;
        this.sellPrice = sellPrice;
        this.canSell = canSell;
        this.canBuy = canBuy;
        this.individualMultiplier = null;
    }

    public boolean equalsLocation(Location location) {
        return l.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())
                && l.distanceSquared(location) < 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StoreSign && ((StoreSign) obj).equalsLocation(this.l);
    }

    public void updateName() {
        BlockState state1 = l.getBlock().getState();
        if (state1 instanceof Sign) {
            Sign state = (Sign) state1;
            String line = null;
            if (this.canSell) {
                line = MainData.getIns().getMessageManager().getMessage("SIGNS_SELL").toString();
            } else if (this.canBuy) {
                line = MainData.getIns().getMessageManager().getMessage("SIGNS_BUY").toString();
            }
            state.setLine(0, line);

            if (this.getItem().getItemMeta().hasDisplayName()) {
                if (this.getItem().getItemMeta().getDisplayName().length() > 32) {
                    state.setLine(1, this.getItem().getItemMeta().getDisplayName().substring(0, 32));
                } else {
                    state.setLine(1, this.getItem().getItemMeta().getDisplayName());
                }
            } else {
                state.setLine(1, String.valueOf(this.getItem().getType().getId()) + ":" + String.valueOf(this.getItem().getData().getData()));
            }

            state.setLine(2, String.valueOf(this.getItem().getAmount()));
            if (this.isCanBuy() && this.isCanSell()) {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_BUY_SELL_PRICE")
                        .format("%buyPrice%", String.valueOf(getPrice()))
                        .format("%sellPrice%", String.valueOf(getSellPrice())).toString());
            } else if (this.isCanBuy() && !this.isCanSell()) {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_BUY_PRICE")
                        .format("%buyPrice%", String.valueOf(getPrice())).toString());
            } else {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_SELL_PRICE")
                        .format("%sellPrice%", String.valueOf(getSellPrice())).toString());
            }
            state.update();
        }

    }

    public double getRankMultiplier(PlayerData d) {
        double rankMultiplier;

        if (getIndividualMultiplier() == null) {
            rankMultiplier = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(d);
        } else {
            RankMultiplier rankMultiplier1 = RankMultiplierMain.getIns().getRankManager().getRankMultiplier(getIndividualMultiplier());
            if (rankMultiplier1 != null) {
                rankMultiplier = rankMultiplier1.getRankMultiplierForPlayer(d);
            } else {
                rankMultiplier = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(d);
            }
        }

        return rankMultiplier;
    }

    public JSONObject save(JSONObject object) {

        object.put("ID", id);
        FileConfiguration fc = new YamlConfiguration();
        fc.set("Item", item);
        object.put("Item", fc.saveToString());
        JSONObject location = new JSONObject();
        new SerializableLocation(l).save(location);
        object.put("Location", location);
        object.put("Price", this.price);
        object.put("SellPrice", this.sellPrice);
        object.put("CanSell", this.canSell);
        object.put("CanBuy", this.canBuy);
        object.put("IndividualMult", this.individualMultiplier);

        return object;
    }


}
