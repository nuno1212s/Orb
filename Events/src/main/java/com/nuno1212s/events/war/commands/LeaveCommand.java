package com.nuno1212s.events.war.commands;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by players");

            return true;
        }

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

        if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

            String clanID = ((ClanPlayer) playerData).getClan();
            if (EventMain.getIns().getWarEvent().isClanRegistered(clanID)) {

                if (EventMain.getIns().getWarEvent().getPlayersRegistered(clanID).contains(playerData.getPlayerID())) {

                    EventMain.getIns().getWarEvent().removePlayer(playerData.getPlayerID());

                } else {

                    MainData.getIns().getMessageManager().getMessage("NOT_REGISTERED_IN_WAR_EVENT")
                            .sendTo(playerData);

                }

            } else {

                MainData.getIns().getMessageManager().getMessage("CLAN_NOT_REGISTERED")
                        .sendTo(playerData);

            }

        }

        return false;
    }
}
