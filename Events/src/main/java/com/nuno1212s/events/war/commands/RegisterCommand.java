package com.nuno1212s.events.war.commands;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by players!");

            return true;
        }

        if (!EventMain.getIns().getWarEvent().canRegisterClan()) {
            MainData.getIns().getMessageManager().getMessage("WAR_EVENT_NOT_ACTIVE")
                    .sendTo(commandSender);

            return true;
        }

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

        if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

            String clanID = ((ClanPlayer) playerData).getClan();

            Clan c = ClanMain.getIns().getClanManager().getClan(clanID);

            if (c.getRank(playerData.getPlayerID()).ordinal() < Clan.Rank.ADMIN.ordinal()) {

                MainData.getIns().getMessageManager().getMessage("CANNOT_REGISTER_CLAN_NO_PERMISSION")
                        .sendTo(commandSender);

                return true;
            }

            if (EventMain.getIns().getWarEvent().isClanRegistered(clanID)) {

                MainData.getIns().getMessageManager().getMessage("CLAN_ALREADY_REGISTERED")
                        .sendTo(commandSender);

                return true;
            }

            EventMain.getIns().getWarEvent().registerClan(c, (Player) commandSender);

        } else {
            MainData.getIns().getMessageManager().getMessage("NO_CLAN").sendTo(commandSender);
        }

        return false;
    }
}
