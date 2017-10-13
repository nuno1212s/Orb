package com.nuno1212s.factions.miningworld.commands;

import com.nuno1212s.factions.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MiningWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player p = (Player) commandSender;

        if (Main.getIns().getMiningWorld().isLoading() || Main.getIns().getMiningWorld().getCurrentMiningWorld() == null) {
            MainData.getIns().getMessageManager().getMessage("MINING_WORLD_LOADING").sendTo(commandSender);
            return true;
        }

        MainData.getIns().getMessageManager().getMessage("TELEPORTED_MINING_WORLD").sendTo(commandSender);
        p.teleport(Main.getIns().getMiningWorld().getCurrentMiningWorld().getSpawnLocation());

        return true;
    }
}
