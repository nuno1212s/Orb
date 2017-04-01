package com.nuno1212s.permissions;

import com.nuno1212s.main.Main;
import com.nuno1212s.mysql.MySqlHandler;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;

import java.util.HashMap;
import java.util.List;

public class PermissionsGroupManager {

    @Getter
    private static PermissionsGroupManager ins = new PermissionsGroupManager();

    public void load() {

        BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                PermissionsAPI.getIns().allgroups = MySqlHandler.getIns().loadGroups();
            }
        });

    }

    public HashMap<String, Boolean> loadPerms(List<String> perms) {
        HashMap<String, Boolean> finalperms = new HashMap<>();
        for (String perm : perms) {
            if (perm.startsWith("-")) {
                perm = perm.substring(1);
                finalperms.put(perm, false);
            } else {
                finalperms.put(perm, true);
            }
        }
        return finalperms;
    }

}
