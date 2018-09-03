package com.nuno1212s.events.war.commands;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.WarEventScheduler;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by players");

            return true;
        }

        if (!EventMain.getIns().getWarEvent().canRegisterClan()) {

            MainData.getIns().getMessageManager().getMessage("WAR_EVENT_NOT_ACTIVE")
                    .sendTo(commandSender);

            // TODO: 27-08-2018 Add time to war event

            return true;
        }

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

        if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

            Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

            if (EventMain.getIns().getWarEvent().isClanRegistered(c.getClanID())) {

                if (EventMain.getIns().getWarEvent().getPlayersRegistered(c.getClanID()).size() >= WarEventScheduler.MAX_START_PLAYERS) {

                    MainData.getIns().getMessageManager().getMessage("CLAN_ALREADY_FULL")
                            .sendTo(commandSender);

                } else {

                    EventMain.getIns().getWarEvent().registerPlayer(c.getClanID(), ((Player) commandSender).getUniqueId());

                }

            } else {

                MainData.getIns().getMessageManager().getMessage("CLAN_NOT_REGISTERED")
                        .sendTo(commandSender);

            }

        }

        return false;
    }
}
