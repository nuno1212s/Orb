package com.nuno1212s.npccommands.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npccommands.main.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListCommandsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("registerCommand")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);

        if (selected == null) {
            commandSender.sendMessage(ChatColor.RED + "Please select an NPC");
            return true;
        }

        List<String> commandsFromNPC = Main.getIns().getNpcManager().getCommandsFromNPC(selected.getEntity().getUniqueId());
        if (commandsFromNPC == null) {
            commandSender.sendMessage(ChatColor.RED + "The NPC has no connected commands");
            return true;
        }

        commandSender.sendMessage(ChatColor.RED + "Commands:");
        commandsFromNPC.forEach(commandSender::sendMessage);

        return true;
    }
}
