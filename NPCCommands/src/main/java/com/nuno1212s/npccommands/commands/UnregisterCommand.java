package com.nuno1212s.npccommands.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npccommands.main.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnregisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (!commandSender.hasPermission("registerCommand")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "/unregisternpc <command>");
            return true;
        }

        NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);

        if (selected == null) {
            commandSender.sendMessage(ChatColor.RED + "Please select an NPC");
            return true;
        }

        StringBuilder cmd = new StringBuilder();

        for (String st : args) {
            cmd.append(st);
            cmd.append(" ");
        }

        if (Main.getIns().getNpcManager().removeCommandFromNPC(selected.getEntity().getUniqueId(), cmd.toString())) {
            commandSender.sendMessage(ChatColor.GREEN + "Command unregistered.");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Command could not be removed");
        }
        return true;
    }
}
