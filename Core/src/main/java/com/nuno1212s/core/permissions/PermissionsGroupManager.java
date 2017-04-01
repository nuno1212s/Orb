package com.nuno1212s.core.permissions;

import com.nuno1212s.core.configmanager.Config;
import com.nuno1212s.core.main.Main;
import lombok.Getter;
import com.nuno1212s.core.mysql.MySqlDB;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PermissionsGroupManager {

    @Getter
    private static PermissionsGroupManager ins = new PermissionsGroupManager();

    @Getter
    private List<PermissionsGroup> servergroups = new ArrayList<>();
    @Getter
    private HashMap<PermissionsGroup, HashMap<String, Boolean>> serverPermissions = new HashMap<>();

    public void load() {

        String servername = Main.getInstance().getServerName();
        String servertype = Main.getInstance().getServerType();

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {

                PermissionsAPI.getIns().allgroups = MySqlDB.getIns().loadGroups();

                for (PermissionsGroup pg : PermissionsAPI.getIns().allgroups) {

                    if (pg.getServerType().equalsIgnoreCase(servertype)) {
                        servergroups.add(pg);
                        continue;
                    }

                    if (pg.getServerName().equalsIgnoreCase(servername)) {
                        servergroups.add(pg);
                        continue;
                    }

                    if (pg.getServerName().equalsIgnoreCase("global")) {
                        servergroups.add(pg);
                        continue;
                    }

                }

                loadServerPermissions();
            }
        });

    }

    public PermissionsGroup getDefault() {
        for (PermissionsGroup pg : getServergroups()) {
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

    public void addPerm(PermissionsGroup pg, String perm) {
        boolean v = true;
        if (perm.startsWith("-")) {
            perm = perm.substring(1);
            v = false;
        }

        HashMap<String, Boolean> perms = pg.getPermissions();
        perms.put(perm, v);
        pg.setPermissions(perms);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () ->
                MySqlDB.getIns().updateGroup(pg)
        );
    }

    public void removePerm(PermissionsGroup pg, String perm) {
        if (perm.startsWith("-")) {
            perm = perm.substring(1);
        }

        HashMap<String, Boolean> perms = pg.getPermissions();
        perms.remove(perm);
        PermissionsGroup n = pg;
        n.setPermissions(perms);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () ->
                MySqlDB.getIns().updateGroup(pg)
        );
    }

    private void loadServerPermissions() {
        Config c = new Config(Main.getInstance(), "/serverpermissions.yml");
        for (PermissionsGroup pg : servergroups) {
            if (!c.keyExists("group." + ChatColor.stripColor(pg.getDisplay()))) {
                c.setListString("group." + ChatColor.stripColor(pg.getDisplay()) + ".permissions", new ArrayList<>());
            }

            HashMap<String, Boolean> finalperms = loadPerms(c.getListString("group." + ChatColor.stripColor(pg.getDisplay()) + ".permissions", Arrays.asList("none")));
            finalperms.putAll(pg.getPermissions());
            serverPermissions.put(pg, finalperms);
            pg.setPermissions(finalperms);
        }
        c.save();
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

    public void updateSecundaryServerGroup(Player p) {

    }

    public void updatePrimaryServerGroup(Player p) {

    }

}
