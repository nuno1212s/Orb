package com.nuno1212s.boosters.boosters;

import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Handles the data for a booster being sold
 */
@AllArgsConstructor
@Getter
public class BoosterData {

    private float multiplier;

    private int quantity;

    private long durationInMillis, price;

    private String customName;

    private BoosterType type;

    private String applicableServer;

    private boolean isCash;

    public ItemStack writeToItem(ItemStack item) {
        NBTCompound nbt = new NBTCompound(item);
        nbt.add("CustomName", this.getCustomName());
        nbt.add("Multiplier", this.getMultiplier());
        nbt.add("Duration", this.getDurationInMillis());
        nbt.add("Price", this.getPrice());
        nbt.add("Quantity", this.getQuantity());
        nbt.add("Type", this.getType().name());
        nbt.add("ApplicableServer", this.getApplicableServer());
        nbt.add("CashItem", this.isCash() ? 1 : 0);

        return nbt.write(item);
    }

    public static BoosterData readFromItem(ItemStack item) {
        NBTCompound nbt = new NBTCompound(item);
        Map<String, Object> values = nbt.getValues();
        String boosterName = (String) values.get("CustomName");
        float multiplier = (Float) values.get("Multiplier");
        long duration = (Long) values.get("Duration");
        long price = (Long) values.get("Price");
        int quantity = (Integer) values.get("Quantity");
        BoosterType t = BoosterType.valueOf((String) values.get("Type"));
        String applicableServer = (String) values.get("ApplicableServer");
        boolean cash = (Integer) values.get("CashItem") == 1;
        return new BoosterData(multiplier, quantity, duration, price, boosterName, t, applicableServer, cash);
    }

}
