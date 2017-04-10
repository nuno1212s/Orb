package com.nuno1212s.fullpvp.economy;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.fullpvp.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Coin command
 */
public class CoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "This command is only for players");
                return true;
            }
            PVPPlayerData d = (PVPPlayerData) MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());
            MainData.getIns().getMessageManager().getMessage("COINS")
                    .format("%coinAmount%", String.valueOf(d.getCoins())).sendTo(commandSender);
            return true;
        } else {
            if (commandSender.isOp() || commandSender.hasPermission("command.coins")) {
                if (args[0].equalsIgnoreCase("get")) {
                    if (args.length < 2) {
                        commandSender.sendMessage(ChatColor.RED + "/coins get <playerName>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        Pair<PlayerData, Boolean> loaded = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);
                        PlayerData d = loaded.getKey();

                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        if (loaded.getValue()) {
                            d = new PVPPlayerData(d);
                            Main.getIns().getMysql().loadPlayerData((PVPPlayerData) d);
                        }

                        MainData.getIns().getMessageManager().getMessage("COINS_OTHERS")
                                .format("%coinAmount%", String.valueOf(((PVPPlayerData) d).getCoins()))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);
                    });

                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/coins set <player> <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {

                        long coins;

                        try {
                            coins = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Coins must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> loaded = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);
                        PlayerData d = loaded.getKey();

                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        if (loaded.getValue()) {
                            d = new PVPPlayerData(d);
                            Main.getIns().getMysql().loadPlayerData((PVPPlayerData) d);
                        }

                        ((PVPPlayerData) d).setCoins(coins);
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                        MainData.getIns().getMessageManager().getMessage("COINS_SET_OTHER")
                                .format("%coinAmount%", String.valueOf(coins))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("COINS_SET_SELF")
                                .format("%coinAmount%", String.valueOf(coins))
                                .sendTo(d);

                        d.save((o) -> {});

                    });

                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/coins add <player> <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        long coins;

                        try {
                            coins = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Coins must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> loaded = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);
                        PlayerData d = loaded.getKey();

                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        if (loaded.getValue()) {
                            d = new PVPPlayerData(d);
                            Main.getIns().getMysql().loadPlayerData((PVPPlayerData) d);
                        }

                        ((PVPPlayerData) d).setCoins(((PVPPlayerData) d).getCoins() + coins);
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                        MainData.getIns().getMessageManager().getMessage("COINS_ADD_OTHER")
                                .format("%coinAmount%", String.valueOf(coins))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("COINS_ADD_SELF")
                                .format("%coinAmount%", String.valueOf(coins))
                                .sendTo(d);

                        d.save((o) -> {});

                    });
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/coin remove <cash>");
                        return true;
                    }

                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        long coins;

                        try {
                            coins = Long.parseLong(args[2]);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Coins must be a number");
                            return;
                        }

                        Pair<PlayerData, Boolean> loaded = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[1]);
                        PlayerData d = loaded.getKey();

                        if (d == null) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                                    .sendTo(commandSender);
                            return;
                        }

                        if (loaded.getValue()) {
                            d = new PVPPlayerData(d);
                            Main.getIns().getMysql().loadPlayerData((PVPPlayerData) d);
                        }

                        ((PVPPlayerData) d).setCoins(((PVPPlayerData) d).getCoins() - coins);
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                        MainData.getIns().getMessageManager().getMessage("COINS_REMOVE_OTHER")
                                .format("%coinAmount%", String.valueOf(coins))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                        MainData.getIns().getMessageManager().getMessage("COINS_REMOVE_SELF")
                                .format("%coinAmount%", String.valueOf(coins))
                                .sendTo(d);

                        d.save((o) -> {});

                    });
                }
            }
        }
        return true;
    }
}
