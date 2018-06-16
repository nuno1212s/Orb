package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Display reload command
 */
public class ReloadDisplayCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"reloadinventory"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class reloadinventory";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("class.reloadinventory")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        Main.getIns().getKitManager().load();
        player.sendMessage(ChatColor.RED + "Inventory reloaded");

    }
}
