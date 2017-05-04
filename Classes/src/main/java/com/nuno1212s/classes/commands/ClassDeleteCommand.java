package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Class delete command
 */
public class ClassDeleteCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"delete"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class delete <class name>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("class.delete")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Kit kit = Main.getIns().getKitManager().getKit(args[1]);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "There is not kit with that name");
            return;
        }

        Main.getIns().getKitManager().removeKit(kit);
        player.sendMessage(ChatColor.RED + "You have removed the kit.");

    }
}
