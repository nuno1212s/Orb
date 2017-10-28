package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerGetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("spawners.get")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be executed through a player");
            return true;
        }

        Player p = (Player) commandSender;

        if (args.length < 1) {
            p.sendMessage(ChatColor.RED + "/spawnerget <spawnerType>");
            return true;
        }

        EntityType type;

        try {
            type = EntityType.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            p.sendMessage(ChatColor.RED + "Mob type does not exist!");
            return true;
        }

        ItemStack item = new ItemStack(Material.MOB_SPAWNER);

        ItemMeta itemMeta = item.getItemMeta();

        String displayName = MainData.getIns().getMessageManager().getMessage(type.name()).toString();

        itemMeta.setDisplayName(displayName);

        item.setItemMeta(itemMeta);

        NBTCompound nbt = new NBTCompound(item);

        nbt.add("MobType", type.name());

        p.getInventory().addItem(nbt.write(item));

        p.sendMessage(ChatColor.RED + "Spawner has been added to your inventory");

        return true;
    }
}
