package com.nuno1212s.command;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Cash command
 */
public class CashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "This command is only for players");
                return true;
            }
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());
            MainData.getIns().getMessageManager().getMessage("CASH")
                    .format("%cashAmount%", String.valueOf(d.getCash())).sendTo(commandSender);
            return true;
        } else {
            if (commandSender.isOp() || commandSender.hasPermission("command.cash")) {
                if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        commandSender.sendMessage(ChatColor.RED + "/cash get <playerName>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);

                        PlayerData d = data.getKey();
                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        MainData.getIns().getMessageManager().getMessage("CASH_OTHERS")
                                .format("%cashAmount%", String.valueOf(d.getCash()))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);
                    });

                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/cash set <player> <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {

                        long cash;

                        try {
                            cash = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Cash must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);

                        PlayerData d = data.getKey();
                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        d.setCash(cash);

                        MainData.getIns().getMessageManager().getMessage("CASH_SET_OTHER")
                                .format("%cashAmount%", String.valueOf(cash))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("CASH_SET_SELF")
                                .format("%cashAmount%", String.valueOf(cash))
                                .sendTo(d);

                        d.save((o) -> {});

                    });

                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/cash add <player> <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        long cash;

                        try {
                            cash = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Cash must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);

                        PlayerData d = data.getKey();
                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        d.setCash(cash);

                        MainData.getIns().getMessageManager().getMessage("CASH_ADD_OTHER")
                                .format("%cashAmount%", String.valueOf(cash))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("CASH_ADD_SELF")
                                .format("%cashAmount%", String.valueOf(cash))
                                .sendTo(d);

                        d.save((o) -> {});

                    });
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/cash remove <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        long cash;

                        try {
                            cash = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Cash must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);

                        PlayerData d = data.getKey();
                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        d.removeCash(cash);

                        MainData.getIns().getMessageManager().getMessage("CASH_REMOVE_OTHER")
                                .format("%cashAmount%", String.valueOf(cash))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("CASH_REMOVE_SELF")
                                .format("%cashAmount%", String.valueOf(cash))
                                .sendTo(d);

                        d.save((o) -> {});

                    });

                }
            }
        }
        return true;
    }
}
