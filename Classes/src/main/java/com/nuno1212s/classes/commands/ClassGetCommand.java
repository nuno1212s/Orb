package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.CommandUtil.Command;
import com.nuno1212s.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Get the class
 */
public class ClassGetCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"get"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class get <className>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Kit kit = Main.getIns().getKitManager().getKit(args[1]);
        if (kit == null) {
            MainData.getIns().getMessageManager().getMessage("NO_KIT_WITH_THAT_NAME").sendTo(player);
            return;
        }

        kit.giveKitTo(player);
    }
}
