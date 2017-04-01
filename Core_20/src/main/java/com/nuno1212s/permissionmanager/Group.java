package com.nuno1212s.permissionmanager;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Group
 */
@AllArgsConstructor
@Getter
public class Group {

    private short groupID;

    private String groupName, groupPrefix, groupSuffix, scoreboardName, applicableServer;

    private GroupType groupType;

    private List<String> permissions;

}
