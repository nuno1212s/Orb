package com.nuno1212s.hub.guis.options;

import java.util.ArrayList;
import java.util.List;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerVisibilityOption {

    public Main m;

    private static PlayerVisibilityOption ins;

    public static PlayerVisibilityOption getIns() {
        return ins;
    }

    public PlayerVisibilityOption(Main m) {
        this.m = m;
        this.ins = this;

        FileConfiguration fc = m.getConfig();
        slot = fc.getInt("Hotbar.PlayerVisibility.Slot", 5);
        on = ConfigUtils.getIns().getItem("Hotbar.PlayerVisibility.Item.On", fc);
        off = ConfigUtils.getIns().getItem("Hotbar.PlayerVisibility.Item.Off", fc);

    }

    private List<String> isHidingPlayers = new ArrayList<String>();

    private ItemStack on;
    private ItemStack off;

    public int slot;

    private boolean getVaule(Player p) {
        return isHidingPlayers.contains(p.getName());
    }

    public boolean checkItem(ItemStack item) {
        if (item.equals(on))
            return true;
        if (item.equals(off))
            return true;
        return false;
    }

    public void changeOption(Player p) {
        if (getVaule(p))
            show(p);
        else
            hide(p);
    }

    public ItemStack getItem(Player p) {
        if (getVaule(p)) {
            return on;
        } else {
            return off;
        }
    }

    public void hide(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("novus.hub.nonHideable")) {
                player.hidePlayer(p);
            }
        }

        if (!isHidingPlayers.contains(player.getName())) {
            isHidingPlayers.add(player.getName());
        }
    }

    public void show(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            player.showPlayer(p);
        }

        if (isHidingPlayers.contains(player.getName())) {
            isHidingPlayers.remove(player.getName());
        }
    }

}
