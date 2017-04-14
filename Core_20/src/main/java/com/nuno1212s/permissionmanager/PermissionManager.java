package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.MainData;
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


    public PermissionManager(boolean bukkit) {
        groups = MainData.getIns().getMySql().getGroups();

        if (bukkit) {
            this.playerPermissions = new PlayerPermissions();
        }

        this.maxGroupsPerPlayer = 6;

    }

    public void updateGroups() {
        groups = MainData.getIns().getMySql().getGroups();
    }

    public static boolean isApplicable(Group g) {
        return g.getGroupType() == GroupType.GLOBAL
                ||
                (g.getGroupType() == GroupType.LOCAL
                && g.getApplicableServer().equalsIgnoreCase(
                        MainData.getIns().getServerManager().getServerType()));
    }

    public Group getDefaultGroup() {
        for (Group group : this.groups) {
            if (group.isDefaultGroup()) {
                return group;
            }
        }
        return null;
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
