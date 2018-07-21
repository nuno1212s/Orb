package com.nuno1212s.clans.commands;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

        player.openInventory(ClanMain.getIns().getInventoryManager().getMainInventory(playerData).buildInventory(playerData));

        return false;
    }
}
