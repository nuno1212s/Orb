package com.nuno1212s.factions.coins;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.factions.main.Main;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

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
            FPlayerData d = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());
            MainData.getIns().getMessageManager().getMessage("COINS")
                    .format("%coinAmount%", NumberFormat.getInstance().format(d.getCoins())).sendTo(commandSender);
            return true;
        } else {
                if (args[0].equalsIgnoreCase("set")) {
                    if (commandSender.isOp() || commandSender.hasPermission("command.coins")) {
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

                            FPlayerData d = getPlayerData(commandSender, args[1]);

                            if (d == null) {
                                return;
                            }

                            d.setCoins(coins);
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                            MainData.getIns().getMessageManager().getMessage("COINS_SET_OTHER")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .format("%playerName%", d.getPlayerName())
                                    .sendTo(commandSender);

                            MainData.getIns().getMessageManager().getMessage("COINS_SET_SELF")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .sendTo(d);

                            d.save((o) -> {
                            });

                        });
                    }

                } else if (args[0].equalsIgnoreCase("add")) {
                    if (commandSender.isOp() || commandSender.hasPermission("command.coins")) {
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

                            FPlayerData d = getPlayerData(commandSender, args[1]);

                            if (d == null) {
                                return;
                            }

                            d.setCoins(d.getCoins() + coins);
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                            MainData.getIns().getMessageManager().getMessage("COINS_ADD_OTHER")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .format("%playerName%", d.getPlayerName())
                                    .sendTo(commandSender);

                            MainData.getIns().getMessageManager().getMessage("COINS_ADD_SELF")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .sendTo(d);

                            d.save((o) -> {
                            });

                        });
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (commandSender.isOp() || commandSender.hasPermission("command.coins")) {
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

                            FPlayerData d = getPlayerData(commandSender, args[1]);

                            if (d == null) {
                                return;
                            }

                            d.setCoins(d.getCoins() - coins);
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(d));

                            MainData.getIns().getMessageManager().getMessage("COINS_REMOVE_OTHER")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .format("%playerName%", d.getPlayerName())
                                    .sendTo(commandSender);

                            MainData.getIns().getMessageManager().getMessage("COINS_REMOVE_SELF")
                                    .format("%coinAmount%", NumberFormat.getInstance().format(coins))
                                    .sendTo(d);

                            d.save((o) -> {
                            });

                        });
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    MainData.getIns().getScheduler().runTaskAsync(() -> {
                        if (!checkDatabase(commandSender)) {
                            return;
                        }

                        LinkedHashMap<String, Long> coinTop = CoinTopCommand.getCoinTop();

                        Message coin_top = MainData.getIns().getMessageManager().getMessage("COIN_TOP");

                        coinTop.forEach(new BiConsumer<String, Long>() {
                            int current = 1;
                            @Override
                            public void accept(String playerName, Long coinAmount) {
                                coin_top.format("%player" + String.valueOf(current) + "%", playerName);
                                coin_top.format("%coinAmount" + String.valueOf(current) + "%", NumberFormat.getInstance().format(coinAmount));
                                current++;
                            }
                        });

                        coin_top.sendTo(commandSender);

                    });
                } else {
                    MainData.getIns().getScheduler().runTaskAsync(() -> {

                        if (!checkDatabase(commandSender)) {
                            return;
                        }

                        FPlayerData d = getPlayerData(commandSender, args[1]);

                        if (d == null) {
                            return;
                        }

                        MainData.getIns().getMessageManager().getMessage("COINS_OTHERS")
                                .format("%coinAmount%", NumberFormat.getInstance().format(((FPlayerData) d).getCoins()))
                                .format("%playerName%", d.getPlayerName())
                                .sendTo(commandSender);

                    });
                }
            }
        return true;
    }

    private boolean checkDatabase(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            FPlayerData playerData = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());
            if (!commandSender.hasPermission("database.fullaccess")) {
                if (playerData.getLastDatabaseAccess() + 5000 > System.currentTimeMillis()) {
                    MainData.getIns().getMessageManager().getMessage("DATABASE_DELAY")
                            .format("%timeLeft%",
                                    new TimeUtil("SS").toTime(playerData.getLastDatabaseAccess() + 5000 - System.currentTimeMillis()))
                            .sendTo(commandSender);
                    return false;
                }
                playerData.setLastDatabaseAccess(System.currentTimeMillis());
            }
        }
        return true;
    }

    private FPlayerData getPlayerData(CommandSender sender, String playerName) {
        Pair<PlayerData, Boolean> loaded = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);
        PlayerData d = loaded.getKey();

        if (d == null) {
            MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED")
                    .sendTo(sender);
            return null;
        }

        if (loaded.getValue()) {
            d = Main.getIns().getMysql().getPlayerData(d);
        }
        return (FPlayerData) d;
    }

}
