package com.nuno1212s.warps.tpamanager.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.tpamanager.TPAInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command must be executed by a player");
            return true;
        }

        Player p = (Player) commandSender;

        if (args.length < 1) {
            MainData.getIns().getMessageManager().getMessage("TPA_CORRECT_USAGE").sendTo(p);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            MainData.getIns().getMessageManager().getMessage("TPA_SPECIFY_RECIPIENT").sendTo(p);
            return true;
        }
        PlayerData sender = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());
        PlayerData recipientData = MainData.getIns().getPlayerManager().getPlayer(recipient.getUniqueId());

        TPAInstance currentTeleport = Main.getIns().getTpaManager().getTeleport(p.getUniqueId());

        if (currentTeleport == null) {
            int timeNeeded = commandSender.hasPermission("warps.instant") ? 0 : 3;

            TPAInstance newTeleport = new TPAInstance(sender, recipientData, timeNeeded, TPAInstance.TeleportType.TPA);
            Main.getIns().getTpaManager().registerTeleport(p.getUniqueId(), newTeleport);
        } else {
            //Cancel previous teleports
            if (currentTeleport.getOriginalTime() + Main.getIns().getTpaManager().getTimeDelay() > System.currentTimeMillis()
                    && !p.hasPermission("warps.instant")) {
                MainData.getIns().getMessageManager().getMessage("TPA_COOLDOWN").sendTo(p);
                return true;
            }

            int timeNeeded = commandSender.hasPermission("warps.instant") ? 0 : 3;

            TPAInstance newTeleport = new TPAInstance(sender, recipientData, timeNeeded, TPAInstance.TeleportType.TPA);
            Main.getIns().getTpaManager().registerTeleport(p.getUniqueId(), newTeleport);
        }

        return true;
    }
}
