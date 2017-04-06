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

        if (permissions.contains("*")) {
            return true;
        }

        boolean contains = permission.contains(".");
        String[] splitString = permission.split("\\.");
        String mainPermissionBody = contains ? splitString[0] : permission;
        String secondaryPermissionBody = contains ? splitString[1] : null;
        for (String s : this.permissions) {
            if (s.contains(".")) {
                String s1 = s.split("\\.")[0];
                if (s1.equalsIgnoreCase(mainPermissionBody)) {
                    String scnd = s.split("\\.")[1];

                    if (scnd.equals("*")) {
                        return true;
                    }

                    if (scnd.equalsIgnoreCase(secondaryPermissionBody)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
