package com.nuno1212s.permissionmanager;

import com.nuno1212s.mysql.MySqlHandler;

import java.util.List;

/**
 * Handles permissions
 */
public class PermissionManager {

    private List<Group> groups;

    public PermissionManager() {
        groups = MySqlHandler.getIns().getGroups();
    }

    public static boolean isApplicable(Group g) {
        return g.getGroupType() == GroupType.GLOBAL;
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
