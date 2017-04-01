package com.nuno1212s.permissions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PermissionsAPI {

    @Getter
    private static PermissionsAPI ins = new PermissionsAPI();

    @Getter
    public List<PermissionsGroup> allgroups = new ArrayList<>();

    public PermissionsGroup getGroup(short id) {
        for (PermissionsGroup group : this.allgroups) {
            if (group.getGroupId() == id) {
                return group;
            }
        }
        return null;
    }

    public PermissionsGroup getDefaultGroup() {
        for (PermissionsGroup pg : allgroups) {
            if (pg.isDefault())
                return pg;
        }
        System.out.println("!!!");
        System.out.println(" ");
        System.out.println("We did not find a default permissions group!");
        System.out.println(" ");
        System.out.println("!!!");
        return null;
    }

}
