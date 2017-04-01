package com.nuno1212s.hub.guis.options;

import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ChatOption {

    public Main m;

    private static ChatOption ins;

    public static ChatOption getIns() {
        return ins;
    }

    public ChatOption(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();
        on = ConfigUtils.getIns().getItem("Inventory.Options.Chat.Item.On", fc);
        off = ConfigUtils.getIns().getItem("Inventory.Options.Chat.Item.Off", fc);
        slot = fc.getInt("Inventory.Options.Chat.Slot", 0);

    }

    private ItemStack on;
    private ItemStack off;

    public int slot;

    public void changeOption(PlayerData playerData) {
        playerData.setChat(!playerData.isChat());
    }

    public ItemStack getItem(PlayerData d) {
        return d.isChat() ? on : off;
    }

}
