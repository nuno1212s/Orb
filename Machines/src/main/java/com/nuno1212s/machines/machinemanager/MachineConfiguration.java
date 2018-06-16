package com.nuno1212s.machines.machinemanager;

import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MachineConfiguration {

    private long baseAmount;

    private long spacing;

    private Machine.MachineType type;

    private boolean cash;

    private long price;

    public MachineConfiguration(JSONObject obj) {

        this.baseAmount = (Long) obj.get("BaseAmount");
        this.spacing = (Long) obj.get("Spacing");
        this.type = Machine.MachineType.valueOf((String) obj.get("Type"));
        this.cash = (Boolean) obj.getOrDefault("Cash", false);
        this.price = (Long) obj.get("Price");

    }

    public ItemStack intoItem(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        compound.add("BaseAmount", this.baseAmount);
        compound.add("Spacing", this.spacing);
        compound.add("Type", type.name());
        //NBT does not support boolean types, use Int types instead
        compound.add("Cash", this.cash ? 1 : 0);
        compound.add("Price", this.price);

        return compound.write(item);
    }

    public static MachineConfiguration fromItem(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        Map<String, Object> values = compound.getValues();

        if (!values.containsKey("BaseAmount")) {
            return null;
        }

        long baseAmount = (long) values.getOrDefault("BaseAmount", 0);
        long spacing = (long) values.getOrDefault("Spacing", Long.MAX_VALUE);
        Machine.MachineType type = Machine.MachineType.valueOf((String) values.getOrDefault("Type", ""));
        long price = (long) values.getOrDefault("Price", Long.MAX_VALUE);
        boolean cash = ((int) values.getOrDefault("Cash", 0) == 1);

        return new MachineConfiguration(baseAmount, spacing, type, cash, price);
    }
}
