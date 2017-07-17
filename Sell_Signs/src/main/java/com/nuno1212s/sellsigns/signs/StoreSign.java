package com.nuno1212s.sellsigns.signs;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.IOException;

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

    public StoreSign(JSONObject object) {
        this.id = (Integer) object.get("ID");
        try {
            this.item = ItemUtils.itemFrom64("Item");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.l = new SerializableLocation((JSONObject) object.get("Location"));
        this.price = (Integer) object.get("Price");
        this.sellPrice = (Integer) object.get("SellPrice");
        this.canSell = (Boolean) object.get("CanSell");
        this.canBuy = (Boolean) object.get("CanBuy");
    }

    public StoreSign(int id, Location l, int price, int sellPrice, boolean canSell, boolean canBuy) {
        this.id = id;
        this.l = l;
        this.price = price;
        this.sellPrice = sellPrice;
        this.canSell = canSell;
        this.canBuy = canBuy;
    }

    public boolean equalsLocation(Location location) {
        return l.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())
                && l.getBlockX() == location.getBlockX()
                && l.getBlockY() == location.getBlockY()
                && l.getBlockZ() == location.getBlockZ();
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
            state.setLine(1, String.valueOf(this.getItem().getType().getId()) + ":" + String.valueOf(this.getItem().getData().getData()));
            state.setLine(2, String.valueOf(this.getItem().getAmount()));
            if (this.isCanBuy() && this.isCanSell()) {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_BUY_SELL_PRICE")
                        .format("%buyPrice%", String.valueOf(getPrice()))
                        .format("%sellPrice%", String.valueOf(getSellPrice())).toString());
            } else if (this.isCanBuy() && ! this.isCanSell()) {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_BUY_PRICE")
                        .format("%buyPrice%", String.valueOf(getPrice())).toString());
            } else {
                state.setLine(3, MainData.getIns().getMessageManager().getMessage("SIGNS_SELL_PRICE")
                        .format("%sellPrice%", String.valueOf(getSellPrice())).toString());
            }
            state.update();
        }

    }

    public JSONObject save(JSONObject object) {

        object.put("ID", id);
        object.put("Item", ItemUtils.itemTo64(item));
        JSONObject location = new JSONObject();
        new SerializableLocation(l).save(location);
        object.put("Location", location);
        object.put("Price", this.price);
        object.put("SellPrice", this.sellPrice);
        object.put("CanSell", this.canSell);
        object.put("CanBuy", this.canBuy);

        return object;
    }


}
