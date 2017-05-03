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

        if (!kit.getPermission().equalsIgnoreCase("")) {
            if (!player.hasPermission(kit.getPermission())) {
                MainData.getIns().getMessageManager().getMessage("NO_KIT_PERMISSION").sendTo(player);
                return;
            }
        }

        /*
        CHECK IF THIS SERVER SUPPORTS KIT USAGES
         */
        PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());
        if (player1 instanceof KitPlayer) {
            KitPlayer player11 = (KitPlayer) player1;
            if (!player11.canUseKit(kit.getId())) {
                MainData.getIns().getMessageManager().getMessage("CANT_USE_KIT")
                        .format("%time%", new TimeUtil("DD days:HH hours:MM minutes:SS seconds").toTime(player11.timeUntilUsage(kit.getId())))
                        .sendTo(player);
                return;
            }
            ((KitPlayer) player1).registerKitUsage(kit.getId(), System.currentTimeMillis());
        }

        kit.addItems(player);
        MainData.getIns().getMessageManager().getMessage("RECEIVED_KIT").format("%kitName%", kit.getClassName()).sendTo(player);

    }
}
