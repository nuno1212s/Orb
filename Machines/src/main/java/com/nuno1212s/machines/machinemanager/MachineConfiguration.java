package com.nuno1212s.machines.machinemanager;

import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.SerializableItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
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

    private ItemStack displayItem, item;

    public MachineConfiguration(JSONObject obj) {

        this.id = ((Long) obj.get("ID")).intValue();
        this.name = ChatColor.translateAlternateColorCodes('&', (String) obj.get("Name"));
        this.baseAmount = (Long) obj.get("BaseAmount");
        this.spacing = ((Long) obj.get("Spacing")) * 1000L;
        this.type = Machine.MachineType.valueOf((String) obj.get("Type"));
        this.cash = (Boolean) obj.getOrDefault("Cash", false);
        this.price = (Long) obj.get("Price");
        this.displayItem = new SerializableItem((JSONObject) obj.get("DisplayItem"));
        this.item = new SerializableItem((JSONObject) obj.get("Item"));
    }

    public ItemStack intoItem(ItemStack item, int amount) {

        Map<String, String> formats = new HashMap<>();

        formats.put("%name%", this.name);
        formats.put("%baseAmount%", String.valueOf(this.baseAmount));
        formats.put("%spacing%", DurationFormatUtils.formatDuration(this.spacing, "mm:ss"));
        formats.put("%type%", MainData.getIns().getMessageManager().getMessage(this.type.name()).toString());
        formats.put("%amount%", String.valueOf(amount));

        ItemStack clone = item.clone();

        clone = ItemUtils.formatItem(clone, formats);

        NBTCompound compound = new NBTCompound(clone);

        compound.add("Configuration", id);
        compound.add("Amount", amount);

        return compound.write(clone);
    }

    public ItemStack getDisplayItem() {

        Map<String, String> formats = new HashMap<>();

        formats.put("%name%", this.name);
        formats.put("%baseAmount%", String.valueOf(this.baseAmount));
        formats.put("%spacing%", DurationFormatUtils.formatDuration(this.spacing, "mm:ss"));
        formats.put("%type%", MainData.getIns().getMessageManager().getMessage(this.type.name()).toString());
        formats.put("%price%", String.valueOf(this.price));

        ItemStack clone = displayItem.clone();

        return ItemUtils.formatItem(clone, formats);
    }

    public static MachineConfiguration fromItem(ItemStack item) {

        return fromItemWithAmount(item).getKey();
    }

    public static Pair<MachineConfiguration, Integer> fromItemWithAmount(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        Map<String, Object> values = compound.getValues();

        if (!values.containsKey("Configuration")) {
            return new Pair<>(null, 0);
        }

        int id = (int) values.get("Configuration");

        int amount = 1;

        if (values.containsKey("Amount")) {
            amount = (int) values.get("Amount");
        }

        return new Pair<>(Main.getIns().getMachineManager().getConfiguration(id), amount);
    }

    public ItemStack getItem() {

        return intoItem(this.item, 1);

    }

    public ItemStack getItem(int amount) {
        return intoItem(this.item, amount);
    }

    public boolean isEquivalent(MachineConfiguration machine) {

        return this.baseAmount == machine.getBaseAmount() && this.spacing == machine.getSpacing()
                && type == machine.getType() && price == machine.getPrice();

    }
}
