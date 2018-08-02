package com.nuno1212s.clans.commands;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import sun.applet.Main;

public class ResetKDRCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender.hasPermission("clans.resetKDR") || commandSender.isOp()) {

            if (args.length < 1) {

                MainData.getIns().getMessageManager().getMessage("TARGET_REQUIRED").sendTo(commandSender);

                return true;
            }

            MainData.getIns().getScheduler().runTaskAsync(() -> {
                Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[0]);

                if (orLoadPlayer.getKey() == null) {

                    MainData.getIns().getMessageManager().getMessage("PLAYER_DOES_NOT_EXIST").sendTo(commandSender);

                    return;
                }

                if (!(orLoadPlayer.getKey() instanceof ClanPlayer)) {
                    orLoadPlayer.setKey(MainData.getIns().getPlayerManager().requestAditionalServerData(orLoadPlayer.getKey()));
                }

                if (orLoadPlayer.getKey() instanceof ClanPlayer) {
                    ((ClanPlayer) orLoadPlayer.getKey()).setDeaths(0);

                    MainData.getIns().getMessageManager().getMessage("RESET_KDR_SUCCESS").sendTo(commandSender);
                    MainData.getIns().getMessageManager().getMessage("KDR_RESET").sendTo(orLoadPlayer.getKey());

                    if (orLoadPlayer.getValue()) {
                        orLoadPlayer.getKey().save((o) -> {});
                    } else {
                        MainData.getIns().getEventCaller().callUpdateInformationEvent(orLoadPlayer.getKey(), PlayerInformationUpdateEvent.Reason.OTHER);
                    }
                }

            });

            return true;
        } else {

            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);

        }

        return true;
    }
}
