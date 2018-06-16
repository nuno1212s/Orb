package com.nuno1212s.npcinbox.commands.entitycommands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Handles entity unregistering
 */
public class UnregisterEntityCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"unregisterentity"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward unregisterentity";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("reward.unregisterentity")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        NPC e = CitizensAPI.getDefaultNPCSelector().getSelected(player);

        if (e == null) {
            player.sendMessage(ChatColor.RED + "No entities in sight!");
            return;
        }

        if (!Main.getIns().getNpcManager().isNPCRegistered(e.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Entity is not registered");
            return;
        }

        Main.getIns().getNpcManager().unregisterNPC(e.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Entity unregistered successfully");

    }
}
