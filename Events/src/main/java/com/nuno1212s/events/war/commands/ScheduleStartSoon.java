package com.nuno1212s.events.war.commands;

import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ScheduleStartSoon implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (commandSender.hasPermission("events.scheduleStart")) {

            if (strings.length == 0) {

                commandSender.sendMessage(ChatColor.RED + "/schedulestart <minutesUntilStart>");

                return true;
            }

            EventMain.getIns().getWarEvent().startDate = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Integer.parseInt(strings[0]));

            commandSender.sendMessage(ChatColor.RED + "Start date changed");

            return true;

        } else {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
        }

        return true;

    }
}
