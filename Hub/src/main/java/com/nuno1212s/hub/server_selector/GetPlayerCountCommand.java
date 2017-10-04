package com.nuno1212s.hub.server_selector;

import com.nuno1212s.main.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetPlayerCountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(MainData.getIns().getServerManager().getServerPlayerCounts().toString());
        return false;
    }
}
