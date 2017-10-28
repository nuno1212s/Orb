package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class SpawnerSetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("spawners.set")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by a player");
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "/spawnerset <entity>");
            return true;
        }

        Player p = (Player) commandSender;

        Block targetBlock = p.getTargetBlock((Set<Material>) null, 5);

        if (targetBlock == null || targetBlock.getType() != Material.MOB_SPAWNER) {
            commandSender.sendMessage(ChatColor.RED + "You are not looking at a mob spawner");
            return true;
        }

        CreatureSpawner state = (CreatureSpawner) targetBlock.getState();
        EntityType t;

        try {
            t = EntityType.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(ChatColor.RED + "Mob type not found!");
            return true;
        }

        state.setSpawnedType(t);
        commandSender.sendMessage(ChatColor.RED + "The spawner has been set");

        return true;
    }
}
