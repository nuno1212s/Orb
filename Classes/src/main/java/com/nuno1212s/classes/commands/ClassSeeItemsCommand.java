package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Class see items command
 *
 * Command to see the items of the class
 */
public class ClassSeeItemsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"seeitems"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class seeitems <class>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Kit k = Main.getIns().getKitManager().getKit(args[1]);

        if (k == null) {
            player.sendMessage(ChatColor.RED + "There is no class with that name");
            return;
        }

        Inventory classItems = k.getClassItems();
        player.openInventory(classItems);
    }
}
