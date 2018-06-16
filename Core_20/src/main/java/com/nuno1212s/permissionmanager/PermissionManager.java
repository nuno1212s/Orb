package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.CheckExpirationTimer;
import com.nuno1212s.permissionmanager.util.PermissionRedisHandler;
import lombok.Getter;

import java.util.*;

/**
 * Handles permissions
 */
public class PermissionManager {

    @Getter
    private List<Group> groups;

    @Getter
    private PlayerPermissions playerPermissions;

    @Getter
    private int maxGroupsPerPlayer;

    private PermissionRedisHandler permissionRedisHandler;

    public PermissionManager(boolean bukkit) {
        groups = MainData.getIns().getMySql().getGroups();

        if (bukkit) {
            this.playerPermissions = new PlayerPermissions();
            MainData.getIns().getScheduler().runTaskTimer(new CheckExpirationTimer(), 0, 20);
        }

        this.permissionRedisHandler = new PermissionRedisHandler();
        this.maxGroupsPerPlayer = 6;

    }

    /**
     * Update the groups
     */
    public void updateGroups() {
        groups = MainData.getIns().getMySql().getGroups();
    }

    /**
     * Add a group
     *
     * @param group The group to add
     */
    public void addGroup(Group group) {
        this.groups.add(group);
        permissionRedisHandler.publishGroupUpdate();
    }

    public static boolean isApplicable(Group g) {
        return g.getGroupType() == GroupType.GLOBAL
                ||
                (g.getGroupType() == GroupType.LOCAL
                        && g.getApplicableServer().equalsIgnoreCase(
                        MainData.getIns().getServerManager().getServerType()));
    }

    /**
     * Get the default group
     * @return
     */
    public Group getDefaultGroup() {

        Group mostFitting = null;

        /*
        If the default group is LOCAL, it has a better priority than GLOBAL
         */

        for (Group group : this.groups) {
            if (group.isDefaultGroup()) {
                if (mostFitting != null) {
                    if (mostFitting.getGroupType() == GroupType.GLOBAL && (group.getGroupType() == GroupType.LOCAL && isApplicable(group))) {
                        mostFitting = group;
                    }
                } else {
                    mostFitting = group;
                }
            }
        }

        return mostFitting;
    }

    /**
     * Modify
     *
     * @param g
     * @param parameter
     * @param variable
     */
    public void modifyGroup(Group g, String parameter, String variable) {

        if (parameter.equalsIgnoreCase("prefix")) {
            g.setGroupPrefix(variable);
        } else if (parameter.equalsIgnoreCase("suffix")) {
            g.setGroupSuffix(variable);
        } else if (parameter.equalsIgnoreCase("scoreboard")) {
            g.setScoreboardName(variable);
        }

        MainData.getIns().getPlayerManager().getPlayers().forEach(playerData -> {
            MainData.getIns().getEventCaller().callGroupUpdateEvent(playerData, g);
        });
        permissionRedisHandler.publishGroupUpdate();
    }

    public Group getGroup(short groupID) {
        for (Group group : this.groups) {
            if (group.getGroupID() == groupID) {
                return group;
            }
        }
        return null;
    }

    public Group getGroup(String groupName) {
        for (Group group : this.groups) {
            if (group.getGroupName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }


}
