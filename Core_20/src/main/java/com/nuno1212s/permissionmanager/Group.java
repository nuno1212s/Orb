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

}
