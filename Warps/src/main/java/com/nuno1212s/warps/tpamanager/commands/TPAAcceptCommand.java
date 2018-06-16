package com.nuno1212s.warps.tpamanager.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.tpamanager.TPAInstance;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAAcceptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command is only for players");
            return true;
        }

        Player p = (Player) commandSender;

        TPAInstance teleport = Main.getIns().getTpaManager().getTeleportFromRecipient(p.getUniqueId());

        if (teleport == null) {
            MainData.getIns().getMessageManager().getMessage("TPA_NO_PENDING").sendTo(commandSender);
            return true;
        }

        teleport.start();

        return true;
    }
}
