package com.nuno1212s.machines.machinemanager;

import com.nuno1212s.machines.main.Main;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MachineConfiguration {

    private int id;

    private String name;

    private long baseAmount;

    private long spacing;

    private Machine.MachineType type;

    private boolean cash;

    private long price;

    private ItemStack item;

    public MachineConfiguration(JSONObject obj) {

        this.id = (Integer) obj.get("ID");
        this.name = (String) obj.get("Name");
        this.baseAmount = (Long) obj.get("BaseAmount");
        this.spacing = (Long) obj.get("Spacing");
        this.type = Machine.MachineType.valueOf((String) obj.get("Type"));
        this.cash = (Boolean) obj.getOrDefault("Cash", false);
        this.price = (Long) obj.get("Price");
        this.item = new SerializableItem((JSONObject) obj.get("Item"));
    }

    public ItemStack intoItem(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        compound.add("Configuration", id);

        return compound.write(item);
    }

    public static MachineConfiguration fromItem(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        Map<String, Object> values = compound.getValues();

        if (!values.containsKey("Configuration")) {
            return null;
        }

        int id = (int) values.get("Configuration");

        return Main.getIns().getMachineManager().getConfiguration(id);
    }

    public ItemStack getItem() {

        return intoItem(this.item);

    }

    public boolean isEquivalent(MachineConfiguration machine) {

        return this.baseAmount == machine.getBaseAmount() && this.spacing == machine.getSpacing()
                && type == machine.getType() && price == machine.getPrice();

    }
}
