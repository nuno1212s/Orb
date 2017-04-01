package com.nuno1212s.core.permissions;

import lombok.Getter;
import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.playermanager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPermissions {

    @Getter
    private static PlayerPermissions ins = new PlayerPermissions();

    private final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    private Field pField;

    public void setServerGroup(UUID uuid, PermissionsGroup pg) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            Main.getInstance().getServerPermissions().setServerGroup(uuid, pg.getGroupId());
        } else {
            this.unregisterPlayer(player);
            Main.getInstance().getServerPermissions().setServerGroup(uuid, pg.getGroupId());
            this.registerPlayer(player);
            Main.getInstance().getServerPermissions().handlePlayerGroupChange(uuid);
        }
    }

    public PermissionsGroup getDisplayGroup(UUID u) {
        PermissionsGroup group = getGroup(u);
        if (group.isOverridesServerGroup()) {
            return group;
        }
        return getServerPlayerGroup(u);
    }

    public PermissionsGroup getServerPlayerGroup(UUID uuid) {
        if (Main.getInstance().getServerPermissions() != null) {
            return PermissionsAPI.getIns().getGroup(Main.getInstance().getServerPermissions().getGroupId(uuid));
        } else {
            return null;
        }
    }

    public PermissionsGroup getGroup(UUID u) {
        return PermissionsAPI.getIns().getGroup(PlayerManager.getIns().getPlayerData(u).getGroupId());
    }

    public PermissionsGroup getGroup(Player p) {
        return getGroup(p.getUniqueId());
    }

    public void registerPlayer(Player player) {
        if (this.permissions.containsKey(player.getUniqueId())) {
            unregisterPlayer(player);
        }
        PermissionAttachment attachment = player.addAttachment(Main.getInstance());
        permissions.put(player.getUniqueId(), attachment);
        calculateAttachment(player);
    }

    public void unregisterPlayer(Player player) {
        if (this.permissions.containsKey(player.getUniqueId())) {
            try {
                player.removeAttachment(this.permissions.get(player.getUniqueId()));
            } catch (IllegalArgumentException ex) {

            }
            this.permissions.remove(player.getUniqueId());
        }
    }

    protected void calculateAttachment(Player player) {
        if (player == null)
            return;

        PermissionAttachment attachment = this.permissions.get(player.getUniqueId());
        if (attachment == null)
            return;

        Map<String, Boolean> values = calculatePlayerPermissions(player);

        values.forEach(attachment::setPermission);

        player.recalculatePermissions();

    }

    private Map<String, Boolean> reflectMap(PermissionAttachment attachment) {
        try {
            if (this.pField == null) {
                this.pField = PermissionAttachment.class.getDeclaredField("permissions");
                this.pField.setAccessible(true);
            }

            return (Map) this.pField.get(attachment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Boolean> calculatePlayerPermissions(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Boolean> perms = new HashMap<>();

        PermissionsGroup pg1 = PermissionsAPI.getIns().getGroup(PlayerManager.getIns().getPlayerData(uuid).getGroupId());
        if (pg1.getPermissions().size() > 0) {
            HashMap<String, Boolean> perms1 = pg1.getPermissions();
            for (String perm1 : perms1.keySet())
                perms.put(perm1, perms1.get(perm1));
        }

        PermissionsGroup pg2 = this.getServerPlayerGroup(player.getUniqueId());
        if (pg2 != null) {
            if (pg2.getPermissions().size() > 0) {
                HashMap<String, Boolean> perms2 = pg2.getPermissions();
                for (String perm2 : perms2.keySet())
                    perms.put(perm2, perms2.get(perm2));
            }
        }

        PermissionsGroup group = getServerPlayerGroup(player.getUniqueId());
        if (group != null) {
            HashMap<String, Boolean> svPerms = PermissionsGroupManager.getIns().getServerPermissions().get(group);
            if (svPerms != null && svPerms.size() > 0) {
                for (String perm3 : svPerms.keySet())
                    perms.put(perm3, svPerms.get(perm3));
            }
        }

        return perms;
    }
}
