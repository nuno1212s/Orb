package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class create command
 */
public class ClassCreateCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"create"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class create <id> <className> <permission> <size>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("class.create")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION")
                    .sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        String className = args[2], permission = args[3];
        int size, id;

        try {
            id = Integer.parseInt(args[1]);
            size = Integer.parseInt(args[4]);
            if (size % 9 != 0) {
                player.sendMessage(ChatColor.RED + "The kit size must be a multiple of 9");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The size must be a number");
            return;
        }

        Kit kit = Main.getIns().getKitManager().getKit(id);
        if (kit != null) {
            player.sendMessage(ChatColor.RED + "A kit with that ID already exists");
            return;
        }

        kit = Main.getIns().getKitManager().getKit(className);

        if (kit != null) {
            player.sendMessage(ChatColor.RED + "A kit with that name already exists");
            return;
        }

        Kit k = new Kit(id, className, permission, new ItemStack[size], new ItemStack(Material.AIR));
        Main.getIns().getKitManager().addKit(k);
        player.sendMessage(ChatColor.GREEN + "Class created. ID: " + id);
    }
}
