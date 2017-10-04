package com.nuno1212s.command;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.GroupType;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the groups command
 */
public class GroupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        /*
        group serverlist
        group globallist
        group setglobal <player> <groupId>
        group setlocal <player> <groupId>
        group mofidy <groupId> <parameter> <value>
        group addpermission <groupId> <permission>
        group removepermission <groupId> <permission>
        group creategroup <groupId> <groupname> <applicableserver> <grouptype> <default> <overrides>
         */
        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.AQUA + "Comandos:");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group serverList");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group globalList");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group setGlobal <player> <groupID> <time>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group setLocal <player> <groupID> <time>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group modify <groupID> <parameter> <value>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group listPermissions <groupID>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group addPermission <groupID> <permission>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group removePermission <groupID> <permission>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group createGroup <groupID> <groupName> <applicableServer> <groupType> <default> <overrides>");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "/group playerInfo <playerName>");
            return true;
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("serverList")) {
            List<Group> groups = MainData.getIns().getPermissionManager().getGroups();
            groups.forEach(group -> {
                if (group.getGroupType() == GroupType.LOCAL) {
                    commandSender.sendMessage(ChatColor.DARK_AQUA + "Group Name: " + group.getGroupName());
                    commandSender.sendMessage(ChatColor.DARK_AQUA + "Group ID: " + group.getGroupID());
                    commandSender.sendMessage("");
                }
            });
        } else if (arg.equalsIgnoreCase("globalList")) {
            List<Group> groups = MainData.getIns().getPermissionManager().getGroups();
            groups.forEach(group -> {
                if (group.getGroupType() == GroupType.GLOBAL) {
                    commandSender.sendMessage(ChatColor.DARK_AQUA + "Group Name: " + group.getGroupName());
                    commandSender.sendMessage(ChatColor.DARK_AQUA + "Group ID: " + group.getGroupID());
                    commandSender.sendMessage("");
                }
            });
        } else if (arg.equalsIgnoreCase("setGlobal")) {
            if (args.length < 4) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group setGlobal <player> <groupID> <time>");
                return true;
            }
            String playerName = args[1];

            short groupID;
            long time;

            try {
                groupID = Short.parseShort(args[2]);
                time = Long.parseLong(args[3]);
                if (time > 0) {
                    time *= 1000;
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "GroupID and time must be numbers");
                return true;
            }

            Pair<PlayerData, Boolean> player = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);

            Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

            if (group == null) {
                commandSender.sendMessage(ChatColor.RED + "The group does not exist.");
                return true;
            }

            PlayerGroupData.EXTENSION_RESULT extension_result = player.getKey().setMainGroup(groupID, time);

            if (extension_result == PlayerGroupData.EXTENSION_RESULT.EXTENDED_CURRENT) {
                MainData.getIns().getMessageManager().getMessage("GROUP_EXTENDED_CURRENT")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            } else if (extension_result == PlayerGroupData.EXTENSION_RESULT.EXTENDED_AND_ACTIVATED) {
                MainData.getIns().getMessageManager().getMessage("GROUP_EXTENDED_NEW")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            } else {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            }
            MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED_OTHER")
                    .format("%newGroup%", group.getGroupName())
                    .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                    .format("%playerName%", player.getKey().getPlayerName())
                    .sendTo(commandSender);

            player.getKey().save((o) -> {

            });

            if (player.getValue()) {
                MainData.getIns().getEventCaller().callUpdateInformationEvent(player.getKey());
            }

            return true;
        } else if (arg.equalsIgnoreCase("setLocal")) {
            if (args.length < 4) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group setLocal <player> <groupID> <time>");
                return true;
            }
            String playerName = args[1];

            short groupID;
            long time;

            try {
                groupID = Short.parseShort(args[2]);
                time = Long.parseLong(args[3]);
                if (time > 0) {
                    time *= 1000;
                }
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "GroupID and time must be numbers");
                return true;
            }

            Pair<PlayerData, Boolean> player = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);

            Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

            if (group == null) {
                commandSender.sendMessage(ChatColor.RED + "The group does not exist.");
                return true;
            }

            PlayerGroupData.EXTENSION_RESULT extension_result = player.getKey().setServerGroup(groupID, time);

            if (extension_result == PlayerGroupData.EXTENSION_RESULT.EXTENDED_CURRENT) {
                MainData.getIns().getMessageManager().getMessage("GROUP_EXTENDED_CURRENT")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            } else if (extension_result == PlayerGroupData.EXTENSION_RESULT.EXTENDED_AND_ACTIVATED) {
                MainData.getIns().getMessageManager().getMessage("GROUP_EXTENDED_NEW")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            } else {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED")
                        .format("%newGroup%", group.getGroupName())
                        .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                        .sendTo(player.getKey());
            }
            MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED_OTHER")
                    .format("%newGroup%", group.getGroupName())
                    .format("%time%", new TimeUtil("mm meses:DD dias").toTime(time))
                    .format("%playerName%", player.getKey().getPlayerName())
                    .sendTo(commandSender);


            player.getKey().save((o) -> {

            });

            if (player.getValue()) {
                MainData.getIns().getEventCaller().callUpdateInformationEvent(player.getKey());
            }

            return true;
        } else if (arg.equalsIgnoreCase("modify")) {
            if (args.length < 4) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group modify <groupID> <parameter> <value>");
                return true;
            }

            MainData.getIns().getScheduler().runTaskAsync(() -> {
                short groupID;

                try {
                    groupID = Short.parseShort(args[1]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "The groupID must be a number");
                    return;
                }

                Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

                if (group == null) {
                    commandSender.sendMessage(ChatColor.RED + "That group does not exist.");
                    return;
                }

                String parameter = args[2], value = ChatColor.translateAlternateColorCodes('&', args[3].replace("__", " "));

                boolean success = MainData.getIns().getMySql().modifyGroup(groupID, parameter, value);
                if (success) {
                    commandSender.sendMessage(ChatColor.RED + "Group modified successfully");
                    MainData.getIns().getPermissionManager().updateGroups();
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Group could not be modified. Check the arguments and try again");
                }
            });

            return true;
        } else if (arg.equalsIgnoreCase("listPermissions")) {
            if (args.length < 2) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group listPermissions <groupID>");
                return true;
            }

            short groupID;

            try {
                groupID = Short.parseShort(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "The groupID must be a number");
                return true;
            }

            Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

            List<String> permissions = group.getPermissions();
            commandSender.sendMessage(ChatColor.DARK_AQUA + "Group Permissions: ");
            commandSender.sendMessage(permissions.toString());

        } else if (arg.equalsIgnoreCase("addPermission")) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group addPermission <groupID> <permission>");
                return true;
            }

            MainData.getIns().getScheduler().runTaskAsync(() -> {
                short groupID;

                try {
                    groupID = Short.parseShort(args[1]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "The groupID must be a number");
                    return;
                }

                String permission = args[2];

                Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

                if (group == null) {
                    commandSender.sendMessage(ChatColor.RED + "That group does not exist.");
                    return;
                }

                group.addPermission(permission);

                boolean added = MainData.getIns().getMySql().modifyGroup(groupID, "PERMISSIONS", group.permissionsToDB());
                if (added) {
                    commandSender.sendMessage(ChatColor.RED + "Permission added successfully");
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Permission could not be added");
                }
            });
        } else if (arg.equalsIgnoreCase("removePermission")) {
            if (args.length < 3) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group removePermission <groupID> <permission>");
                return true;
            }

            MainData.getIns().getScheduler().runTaskAsync(() -> {
                short groupID;

                try {
                    groupID = Short.parseShort(args[1]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "The groupID must be a number");
                    return;
                }

                String permission = args[2];

                Group group = MainData.getIns().getPermissionManager().getGroup(groupID);

                if (group == null) {
                    commandSender.sendMessage(ChatColor.RED + "That group does not exist.");
                    return;
                }

                group.removePermission(permission);

                boolean added = MainData.getIns().getMySql().modifyGroup(groupID, "PERMISSIONS", group.permissionsToDB());
                if (added) {
                    commandSender.sendMessage(ChatColor.RED + "Permission removed successfully");
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Permission could not be removed");
                }
            });
        } else if (arg.equalsIgnoreCase("creategroup")) {
            if (args.length < 7) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group createGroup <groupID> <groupName>" +
                        " <applicableServer> <groupType> <default> <overrides>");
                return true;
            }

            short groupID;
            boolean isDefault, overrides;
            String groupName = args[2], applicableServer = args[3];
            GroupType groupType;

            try {
                groupID = Short.parseShort(args[1]);
                isDefault = Boolean.parseBoolean(args[5]);
                overrides = Boolean.parseBoolean(args[6]);
                groupType = GroupType.valueOf(args[4]);
            } catch (Exception e) {
                commandSender.sendMessage(ChatColor.RED + "Failed to load the group information");
                return true;
            }

            Group group = MainData.getIns().getPermissionManager().getGroup(groupID);
            if (group != null) {
                commandSender.sendMessage(ChatColor.RED + "That group id has already been taken");
                return true;
            }

            Group nGroup = new Group(groupID, groupName, "", "", "", applicableServer
                    , isDefault, groupType, new ArrayList<>(), overrides);

            MainData.getIns().getPermissionManager().addGroup(nGroup);

            MainData.getIns().getScheduler().runTaskAsync(() -> {
                MainData.getIns().getMySql().addGroup(nGroup);
                commandSender.sendMessage(ChatColor.RED + "Added group to database.");
            });

        } else if (arg.equalsIgnoreCase("playerInfo")) {
            if (args.length < 2) {
                commandSender.sendMessage(ChatColor.DARK_AQUA + "/group playerInfo <playerName>");
                return true;
            }

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(args[1]);

            if (d == null) {
                commandSender.sendMessage(ChatColor.RED + "Player is not online.");
                return true;
            }

            Group mainGroup = d.getMainGroup();

            Group representingGroup = d.getRepresentingGroup();

            Group serverGroup = MainData.getIns().getPermissionManager().getGroup(d.getServerGroup());

            commandSender.sendMessage("Main Group: " + mainGroup.getGroupPrefix() + "(" + mainGroup.getGroupName() + ")");
            commandSender.sendMessage("Server group: " + serverGroup.getGroupPrefix() + "(" + serverGroup.getGroupName() + ")");
            commandSender.sendMessage("Representing group: " + representingGroup.getGroupPrefix() + "(" + representingGroup.getGroupName() + ")");

        }
        return true;
    }
}
