package com.nuno1212s.hub.commands;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.npcs.NPC;
import com.nuno1212s.main.MainData;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RegisterNPCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!commandSender.hasPermission("addNpc")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player p = (Player) commandSender;

        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "/registernpc <server> <displayName>");
            return true;
        }

        net.citizensnpcs.api.npc.NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);

        if (selected == null) {
            p.sendMessage(ChatColor.RED + "You must select an NPC");
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < strings.length; i++) {
            builder.append(strings[i]);

            if (i == strings.length - 1) {
                continue;
            }

            builder.append(" ");
        }

        if (Main.getIns().getNpcManager().getNPC((LivingEntity) selected.getEntity()) != null) {
            p.sendMessage(ChatColor.RED + "That NPC is already registered");
            return true;
        }

        NPC npc = Main.getIns().getNpcManager().addNPC((LivingEntity) selected.getEntity(), strings[0], ChatColor.translateAlternateColorCodes('&', builder.toString()));

        npc.updateNPC();

        p.sendMessage(ChatColor.RED + "Registered NPC");

        return true;
    }


}
