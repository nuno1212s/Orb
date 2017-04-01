package com.nuno1212s.core.permissions;


import com.nuno1212s.core.main.Main;
import lombok.Getter;
import com.nuno1212s.core.mysql.MySqlDB;
import org.bukkit.Bukkit;

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

    public void addGroup(PermissionsGroup pg) {
        allgroups.add(pg);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () ->
                MySqlDB.getIns().updateGroup(pg)
        );

        PermissionsGroupManager.getIns().getServergroups().add(pg);

    }

}
