package com.nuno1212s.permissionmanager;

import lombok.Getter;

import java.util.List;

/**
 * Group
 */
@Getter
public class Group {

    private short groupID;

    private String groupName, groupPrefix, groupSuffix, scoreboardName, applicableServer;

    private boolean defaultGroup;

    private GroupType groupType;

    private List<String> permissions;

    public Group(short groupID, String groupName, String groupPrefix, String groupSuffix
            , String scoreboardName, String applicableServer, boolean defaultGroup
            , GroupType groupType, List<String> permissions) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupPrefix = groupPrefix;
        this.groupSuffix = groupSuffix;
        this.scoreboardName = scoreboardName;
        this.defaultGroup = defaultGroup;
        this.applicableServer = applicableServer;
        this.groupType = groupType;
        this.permissions = permissions;

    }

    public boolean hasPermission(String permission) {
        String mainPermissionBody = permission.contains(".") ? permission.split(".")[0] : permission;
        String secondaryPermissionBody = permission.contains(".") ? permission.split(".")[1] : null;
        for (String s : this.permissions) {
            if (s.contains(".")) {
                if (s.split(".")[0].equalsIgnoreCase(mainPermissionBody)) {
                    String scnd = s.split(".")[1];

                    if (scnd.equals("*")) {
                        return true;
                    }

                    return scnd.equalsIgnoreCase(secondaryPermissionBody);
                }
            } else {
                return s.equalsIgnoreCase(permission);
            }
        }
        return false;
    }

}
