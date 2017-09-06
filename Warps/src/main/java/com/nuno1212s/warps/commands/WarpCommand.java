package com.nuno1212s.warps.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.warpmanager.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages the warp commands
 */
public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("warp")) {
            if (args.length < 1) {
                if (commandSender.hasPermission("warps.seeall")) {
                    StringBuilder msg = new StringBuilder(ChatColor.YELLOW + "Warps: ");
                    for (Warp warp : Main.getIns().getWarpManager().getWarps()) {
                        msg.append(warp.getWarpName());
                        msg.append(",");
                    }
                    commandSender.sendMessage(msg.toString());
                } else {
                    MainData.getIns().getMessageManager().getMessage("SPECIFY_WARP").sendTo(commandSender);
                }
                return true;
            }

            Player p = (Player) commandSender;

            String warp = args[1];
            Warp w = Main.getIns().getWarpManager().getWarp(warp);
            if (w == null) {
                MainData.getIns().getMessageManager().getMessage("WARP_DOES_NOT_EXIST").sendTo(commandSender);
                return true;
            }

            if (!commandSender.hasPermission(w.getPermission())) {
                MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
                return true;
            }

            if (Main.getIns().getWarpManager().getWarpTimer().isWarping(p.getUniqueId())) {
                Main.getIns().getWarpManager().getWarpTimer().cancelWarp(p.getUniqueId());
                MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_ANOTHER_WARP").sendTo(p);
            }

            if (w.isDelay() && !p.hasPermission("warp.instant")) {
                Main.getIns().getWarpManager().getWarpTimer().registerWarp(p.getUniqueId(), w);
                MainData.getIns().getMessageManager().getMessage("WARPS_WARPING_IN").format("%time%", String.valueOf(w.getDelayInSeconds())).sendTo(p);
            } else {
                p.teleport(w.getL());
                MainData.getIns().getMessageManager().getMessage("WARPS_WARPED").sendTo(p);
            }


        } else if (command.getName().equalsIgnoreCase("setwarp")) {
            if (!(commandSender instanceof Player)) {
                return true;
            }
            Player p = (Player) commandSender;
            if (p.hasPermission("warps.setwarp")) {
                if (args.length < 4) {
                    //setwarp <name> <delayed> <console> <delay>
                    p.sendMessage(ChatColor.RED + "/setwarp <name> <delayed> <console> <delay>");
                    return true;
                }
                String name = args[0];
                boolean delayed = Boolean.parseBoolean(args[1]), console = Boolean.parseBoolean(args[2]);
                int delay = Integer.parseInt(args[3]);
                Main.getIns().getWarpManager().registerWarp(name, p.getLocation(), delayed, delay, console);
                p.sendMessage(ChatColor.RED + "Adicionaste o warp " + name);
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("delwarp")) {
            Player p = (Player) commandSender;
            if (p.hasPermission("warps.delwarp")) {
                if (args.length == 0) {
                    commandSender.sendMessage(ChatColor.RED + "Uso correto: /delwarp <name>");
                    return true;
                }
                String name = args[0];
                Warp warp = Main.getIns().getWarpManager().getWarp(name);
                if (warp == null) {
                    commandSender.sendMessage(ChatColor.RED + "Warp não existe");
                    return true;
                }
                Main.getIns().getWarpManager().removeWarp(warp);
                commandSender.sendMessage(ChatColor.RED + "Warp apagado.");
            }

        } else if (command.getName().equalsIgnoreCase("warps")) {
            if (!(commandSender instanceof Player)) {
                return true;
            }

            ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getDefaultInventory());

        } else {
            Warp w = Main.getIns().getWarpManager().getWarp(command.getName());

            if (w == null) {
                MainData.getIns().getMessageManager().getMessage("WARP_DOES_NOT_EXIST").sendTo(commandSender);
                return true;
            }

            Player p = (Player) commandSender;

            if (w.isRequiredConsole()) {
                MainData.getIns().getMessageManager().getMessage("WARPS_CONSOLE_ONLY").sendTo(commandSender);
                return true;
            }

            if (!p.hasPermission(w.getPermission())) {
                MainData.getIns().getMessageManager().getMessage("WARPS_NO_PERMISSION").sendTo(commandSender);
                return true;
            }

            if (Main.getIns().getWarpManager().getWarpTimer().isWarping(p.getUniqueId())) {
                Main.getIns().getWarpManager().getWarpTimer().cancelWarp(p.getUniqueId());
                MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_ANOTHER_WARP").sendTo(commandSender);
            }

            if (w.isDelay() && !p.hasPermission("warps.instant")) {
                Main.getIns().getWarpManager().getWarpTimer().registerWarp(p.getUniqueId(), w);
                MainData.getIns().getMessageManager().getMessage("WARPS_WARPING_IN").format("%time%", String.valueOf(w.getDelayInSeconds())).sendTo(p);
            } else {
                p.teleport(w.getL());
                MainData.getIns().getMessageManager().getMessage("WARPS_WARPED").sendTo(p);
            }

        }
        return true;
    }

}
