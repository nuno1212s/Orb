package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Opens the reward inventory
 */
public class OpenRewardInventoryCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"open"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward open";
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openInventory(Main.getIns().getInventoryManager().buildRewardInventoryForPlayer(MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId())));
    }
}
