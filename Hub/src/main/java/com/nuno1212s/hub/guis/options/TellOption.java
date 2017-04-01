package com.nuno1212s.hub.guis.options;

import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class TellOption {

    public Main m;

    private static TellOption ins;

    public static TellOption getIns() {
        return ins;
    }

    public TellOption(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();
        on = ConfigUtils.getIns().getItem("Inventory.Options.Tell.Item.On", fc);
        off = ConfigUtils.getIns().getItem("Inventory.Options.Tell.Item.Off", fc);
        slot = fc.getInt("Inventory.Options.Tell.Slot", 0);

    }

    private ItemStack on;
    private ItemStack off;

    public int slot;

    public void changeOption(PlayerData d) {
        d.setTell(!d.isTell());
    }

    public ItemStack getItem(PlayerData d) {
        return d.isTell() ? on : off;
    }

}
