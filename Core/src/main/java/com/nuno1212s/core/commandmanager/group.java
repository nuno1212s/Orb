package com.nuno1212s.core.commandmanager;

import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.permissions.PermissionsAPI;
import com.nuno1212s.core.permissions.PermissionsGroup;
import com.nuno1212s.core.permissions.PermissionsGroupManager;
import com.nuno1212s.core.permissions.PlayerPermissions;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

//@SuppressWarnings("LossyEncoding")
public class group implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        CommandSender p = commandSender;

        if (!(p instanceof ConsoleCommandSender) && !p.hasPermission("novus.core.command.group")) {
            Main.getIns().getMessages().getMessage("WithoutPermission").sendTo(p);
             return true;
        }

        if (args.length < 1) {

            p.sendMessage(ChatColor.AQUA + "Comandos: ");

            p.sendMessage(ChatColor.DARK_AQUA + "- /group serverlist");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group alllist"); // especificar primary? server? type?

            p.sendMessage(ChatColor.DARK_AQUA + "- /group permlist <groupid>");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group addglobalperm <groupid> <perm>");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group removeglobalperm <groupid> <perm>");

            p.sendMessage(ChatColor.DARK_AQUA + "- /group setprimary <player> <groupid>");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group setsecondary <player> <groupid>"); //exists in this server?

            p.sendMessage(ChatColor.DARK_AQUA + "- /group check <player>");

            p.sendMessage(ChatColor.DARK_AQUA + "- /group create <id> <display>");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group modify <id> <parameter> <vaule>");
            p.sendMessage(ChatColor.DARK_AQUA + "- /group checkgroup <id>");

            return true;
        }

        String sub = args[0];

        if (sub.equalsIgnoreCase("serverlist")) {

            p.sendMessage(ChatColor.DARK_AQUA + "Server groups:");
            for (PermissionsGroup pg : PermissionsGroupManager.getIns().getServergroups())
                p.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + "(" + pg.getGroupId() + ") " + pg.getDisplay());

            return true;
        }

        if (sub.equalsIgnoreCase("alllist")) {

            p.sendMessage(ChatColor.DARK_AQUA + "All registered groups in mysql:");
            for (PermissionsGroup pg : PermissionsAPI.getIns().getAllgroups())
                p.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + "(" + pg.getGroupId() + ") " + pg.getDisplay());

            return true;
        }

        if (sub.equalsIgnoreCase("permlist")) {
            if (args.length != 2) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group permlist <groupid>");
                return true;
            }
            short groupid = 0;
            try { groupid = Short.parseShort(args[1]); } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            p.sendMessage(ChatColor.DARK_AQUA + "(Global) All server permissions:");
            HashMap<String, Boolean> perms1 = pg.getPermissions();
            for (String p1 : perms1.keySet()) {
                p.sendMessage(ChatColor.AQUA + "- " + p1 + " = " + perms1.get(p1).toString());
            }

            p.sendMessage(ChatColor.DARK_AQUA + "(Particular) Server permissions:");
            HashMap<String, Boolean> perms2 = PermissionsGroupManager.getIns().getServerPermissions().get(pg);
            for (String p2 : perms2.keySet()) {
                p.sendMessage(ChatColor.AQUA + "- " + p2 + " = " + perms2.get(p2).toString());
            }

            return true;
        }

        if (sub.equalsIgnoreCase("addglobalperm")) {
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group addglobalperm <groupid> <perm>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            String perm = args[2];
            PermissionsGroupManager.getIns().addPerm(pg, perm);
            p.sendMessage(ChatColor.GREEN + "Successfully added " + perm + " to group " + pg.getDisplay() + ChatColor.GREEN + ".");

            return true;
        }

        if (sub.equalsIgnoreCase("removeglobalperm")) {
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group removeglobalperm <groupid> <perm>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            String perm = args[2];
            PermissionsGroupManager.getIns().removePerm(pg, perm);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getIns(), () ->
                    MySqlDB.getIns().updateGroup(pg)
            );
            p.sendMessage(ChatColor.GREEN + "Successfully removed " + perm + " to group " + pg.getDisplay() + ChatColor.GREEN + ".");

            return true;
        }

        if (sub.equalsIgnoreCase("setprimary")) {
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group setprimary <player> <groupid>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[2]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            String player = args[1];
            Player playerD = Bukkit.getPlayer(player);
            PlayerData pd;
            if (playerD == null || !playerD.isOnline()) {
                pd = PlayerManager.getIns().getPlayerID(player);
                if (pd == null) {
                    p.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
            } else {
                pd = PlayerManager.getIns().getPlayerData(playerD.getUniqueId());
            }

            pd.setGroupId(groupid);
            p.sendMessage(ChatColor.GREEN + "Successfully changed " + player + " global group to " + pg.getDisplay() + ChatColor.GREEN + ".");
            if (playerD != null && playerD.isOnline()) {
                Main.getIns().getMessages().getMessage("UpdatedGroup").format("%groupo%", pg.getDisplay()).sendTo(playerD);
                Main.getIns().getServerPermissions().handlePlayerGroupChange(playerD.getUniqueId());
            }

            return true;
        }

        if (sub.equalsIgnoreCase("setsecondary")) {
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group setsecondary <player> <groupid>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[2]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            if (!PermissionsGroupManager.getIns().getServergroups().contains(pg)) {
                p.sendMessage(ChatColor.RED + "This group is not avaible in this server.");
                return true;
            }

            String player = args[1];
            Player playerD = Bukkit.getPlayer(player);
            PlayerData pd;
            if (playerD == null || !playerD.isOnline()) {
                pd = PlayerManager.getIns().getPlayerID(player);
                if (pd == null) {
                    p.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
            } else {
                pd = PlayerManager.getIns().getPlayerData(playerD.getUniqueId());
            }

            PlayerPermissions.getIns().setServerGroup(pd.getId(), pg);
            p.sendMessage(ChatColor.GREEN + "Successfully changed " + player + " server group to " + pg.getDisplay() + ChatColor.GREEN + ".");
            if (playerD != null && playerD.isOnline()) {
                Main.getIns().getMessages().getMessage("UpdatedGroup").format("%grupo%", pg.getDisplay()).sendTo(playerD);
            }

            return true;
        }

        if (sub.equalsIgnoreCase("check")) {
            if (args.length != 2) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group check <player>");
                return true;
            }

            String player = args[1];
            PlayerData pd = PlayerManager.getIns().getPlayerID(player);
            if (pd == null) {
                p.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            PermissionsGroup gpg = PermissionsAPI.getIns().getGroup(pd.getGroupId());
            PermissionsGroup spg = PlayerPermissions.getIns().getServerPlayerGroup(pd.getId());
            String a;
            if (spg == null) {
                a = "none";
            } else {
                a = spg.getGroupId() + "";
            }

            p.sendMessage(ChatColor.DARK_AQUA + "Player group informations:");
            p.sendMessage(ChatColor.DARK_AQUA + "Global group: " + ChatColor.AQUA + gpg.getGroupId());
            p.sendMessage(ChatColor.DARK_AQUA + "Server group: " + ChatColor.AQUA + a);

            return true;
        }

        if (sub.equalsIgnoreCase("checkgroup")) {
            if (args.length != 2) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group checkgroup <groupid>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            p.sendMessage(ChatColor.DARK_AQUA + "Group Informations:");

            p.sendMessage(ChatColor.DARK_AQUA + "ID: " + ChatColor.AQUA + pg.getGroupId());
            p.sendMessage(ChatColor.DARK_AQUA + "Display: " + ChatColor.AQUA + pg.getDisplay());
            p.sendMessage(ChatColor.DARK_AQUA + "Server: " + ChatColor.AQUA + pg.getServerName());
            p.sendMessage(ChatColor.DARK_AQUA + "Server Type: " + ChatColor.AQUA + pg.getServerType());
            p.sendMessage(ChatColor.DARK_AQUA + "Prefix: " + ChatColor.AQUA + pg.getPrefix());
            p.sendMessage(ChatColor.DARK_AQUA + "Suffix: " + ChatColor.AQUA + pg.getSuffix());
            p.sendMessage(ChatColor.DARK_AQUA + "Is Default: " + ChatColor.AQUA + pg.isDefault());
            p.sendMessage(ChatColor.DARK_AQUA + "Global Permissions: ");
            for (String perm : pg.getPermissions().keySet()) {
                if (!pg.getPermissions().get(perm))
                    p.sendMessage(ChatColor.AQUA + "- " + perm + ":" + ChatColor.RED + " false");
                else
                    p.sendMessage(ChatColor.AQUA + "- " + perm + ":" + ChatColor.GREEN + " true");
            }

            return true;
        }

        if (sub.equalsIgnoreCase("create")) {
            if (args.length != 3) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group create <id> <display>");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }

            for (PermissionsGroup pg : PermissionsAPI.getIns().getAllgroups()) {
                if (pg.getGroupId() == groupid) {
                    p.sendMessage(ChatColor.RED + "This group id is in use.");
                    return true;
                }
            }

            PermissionsGroup pg = new PermissionsGroup(Main.getIns().getServerName(), "", groupid, false, args[2], "", "", new HashMap<>(), false, args[2]);
            PermissionsAPI.getIns().addGroup(pg);
            p.sendMessage(ChatColor.GREEN + "Group " + pg.getDisplay() + ChatColor.GREEN + " successfully created.");

            return true;
        }

        if (sub.equalsIgnoreCase("modify")) {
            if (args.length != 4) {
                p.sendMessage(ChatColor.RED + "Correct usage: /group modify <id> <parameter> <vaule>");
                p.sendMessage(ChatColor.DARK_AQUA + "Parameter list: " + ChatColor.AQUA + "display, prefix, suffix, servername, servertype, isdefault, scoreboardDisplay");
                return true;
            }
            short groupid = 0;
            try {
                groupid = Short.parseShort(args[1]);
            } catch (Exception ex) {
                p.sendMessage(ChatColor.RED + "Group id must be an integer number.");
                return true;
            }
            PermissionsGroup pg = PermissionsAPI.getIns().getGroup(groupid);
            if (pg == null) {
                p.sendMessage(ChatColor.RED + "Group not found.");
                return true;
            }

            String param = args[2];
            String vaule = args[3];

            if (param.equalsIgnoreCase("display")) {
                pg.setDisplay(vaule);
            } else if (param.equalsIgnoreCase("prefix")) {
                pg.setPrefix(vaule);
            } else if (param.equalsIgnoreCase("suffix")) {
                pg.setSuffix(vaule);
            } else if (param.equalsIgnoreCase("servername")) {
                pg.setServerName(vaule);

                if (!vaule.equalsIgnoreCase("global")) {
                    if (vaule.equalsIgnoreCase(Main.getIns().getServerName()))
                        if (PermissionsGroupManager.getIns().getServergroups().contains(pg))
                            PermissionsGroupManager.getIns().getServergroups().add(pg);
                }

            } else if (param.equalsIgnoreCase("servertype")) {
                pg.setServerType(vaule);
            } else if (param.equalsIgnoreCase("isdefault")) {
                pg.setIsDefault(Boolean.valueOf(vaule));
            } else if (param.equalsIgnoreCase("scoreboardDisplay")) {
                pg.setScoreboardName(vaule);
            } else {
                p.sendMessage(ChatColor.RED + "Parameter not found.");
                return true;
            }

            Bukkit.getScheduler().runTaskAsynchronously(Main.getIns(), () ->
                    MySqlDB.getIns().updateGroup(pg)
            );
            p.sendMessage(ChatColor.GREEN + "Parameter '" + param.toLowerCase() + ChatColor.GREEN + "' set to '" + vaule + ChatColor.GREEN + "' in group: " + pg.getGroupId());

            return true;
        }

        p.sendMessage(ChatColor.RED + "Subcommand not found.");
        return true;
    }

}
