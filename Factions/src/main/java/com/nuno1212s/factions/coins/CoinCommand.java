package com.nuno1212s.factions.coins;


import com.nuno1212s.factions.playerdata.FPlayerData;

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
                } else if (args[0].equalsIgnoreCase("enviar") || args[0].equalsIgnoreCase("send")) {
                    if (args.length < 3) {
                        commandSender.sendMessage(ChatColor.RED + "/coins enviar <player> <quantidade>");
                        return true;
                    }

                    String playerName = args[1];

                    long coins;

                    try {
                        coins = Long.parseLong(args[2]);

                        if (coins <= 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException e) {
                        MainData.getIns().getMessageManager().getMessage("COINS_NUMBER_POSITIVE").sendTo(commandSender);
                        return true;
                    }

                    FPlayerData player = (FPlayerData) MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

                    if (player.getCoins() < coins) {
                        MainData.getIns().getMessageManager().getMessage("NOT_ENOUGH_COINS").sendTo(commandSender);
                        return true;
                    }

                    Pair<PlayerData, Boolean> playerData = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);

                    if (playerData.getKey() == null) {
                        MainData.getIns().getMessageManager().getMessage("PLAYER_NEVER_JOINED").sendTo(commandSender);
                        return true;
                    }

                    if (playerData.value()) {
                        PlayerInformationLoadEvent event = new PlayerInformationLoadEvent(playerData.key());

                        Bukkit.getServer().getPluginManager().callEvent(event);

                        if (!(event.getPlayerInfo() instanceof FPlayerData)) {
                            MainData.getIns().getMessageManager().getMessage("PLAYER_LOAD_ERROR").sendTo(commandSender);
                            return true;
                        }

                        FPlayerData playerInfo = (FPlayerData) event.getPlayerInfo();

                        playerInfo.setCoins(playerInfo.getCoins() + coins);

                        playerInfo.save((o) -> {});

                        player.setCoins(player.getCoins() - coins);

                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(player));
                    } else {
                        FPlayerData playerInfo = (FPlayerData) playerData.getKey();

                        playerInfo.setCoins(playerInfo.getCoins() + coins);

                        MainData.getIns().getMessageManager().getMessage("COINS_RECEIVED")
                                .format("%player%", player.getNameWithPrefix())
                                .format("%coins%", String.valueOf(coins)).sendTo(playerInfo);

                        player.setCoins(player.getCoins() - coins);

                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(player));

                        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(playerInfo));
                    }

                    MainData.getIns().getMessageManager().getMessage("COINS_SENT")
                            .format("%player%", playerData.getKey().getNameWithPrefix())
                            .format("%coins%", String.valueOf(coins)).sendTo(player);

                } else {
                    MainData.getIns().getScheduler().runTaskAsync(() -> {

                        if (!checkDatabase(commandSender)) {
                            return;
                        }

                        FPlayerData d = getPlayerData(commandSender, args[0]);

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
