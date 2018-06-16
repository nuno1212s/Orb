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

public class UnregisterNPCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!commandSender.hasPermission("removeNPC")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player p = (Player) commandSender;

        net.citizensnpcs.api.npc.NPC selected = CitizensAPI.getDefaultNPCSelector().getSelected(commandSender);

        if (selected == null) {
            p.sendMessage(ChatColor.RED + "You must select an NPC");
            return true;
        }


        NPC npc = Main.getIns().getNpcManager().getNPC((LivingEntity) selected.getEntity());

        if (npc == null) {
            p.sendMessage(ChatColor.RED + "That NPC is not registered");
            return true;
        }

        Main.getIns().getNpcManager().removeNPC(npc);
        p.sendMessage(ChatColor.RED + "Unregistered NPC");

        return false;
    }
}
