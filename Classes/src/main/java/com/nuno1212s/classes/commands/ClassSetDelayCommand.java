package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Change the delay on a class
 */
public class ClassSetDelayCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setdelay"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class setdelay <id> <delay>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("class.setdelay")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        int id;
        long delay;

        try {
            id = Integer.parseInt(args[1]);
            delay = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "ID and delay must be numbers");
            return;
        }

        Kit k = Main.getIns().getKitManager().getKit(id);
        if (k == null) {
            player.sendMessage(ChatColor.RED + "A kit with that id does not exist");
            return;
        }

        k.setDelay(delay * 1000);
        player.sendMessage(ChatColor.RED + "You have set the delay of the kit " + k.getClassName() + " to " + delay + " seconds");
    }
}
