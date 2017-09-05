package com.nuno1212s.npcinbox.commands.entitycommands;

import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Handles entity registration
 */
public class RegisterEntityCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"registerentity"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward registerentity";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.hasPermission("reward.registerentity")) {
            Entity e = Main.getIns().getNpcManager().getEntityInLineOfSight(player, 5);
            if (e == null) {
                player.sendMessage(ChatColor.RED + "No entities in your field of view!");
                return;
            }

            Main.getIns().getNpcManager().registerNPC(e.getUniqueId());
            player.sendMessage(ChatColor.RED + "The NPC has been registered");

        }
    }
}
