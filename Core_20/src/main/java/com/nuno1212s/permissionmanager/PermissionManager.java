package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

/**
 * Handles permissions
 */
public class PermissionManager {

    private List<Group> groups;

    private Map<UUID, PermissionAttachment> playerAttachments;

    public PermissionManager() {
        playerAttachments = new HashMap<>();
        groups = Main.getIns().getMySql().getGroups();
    }

    public static boolean isApplicable(Group g) {
        return g.getGroupType() == GroupType.GLOBAL
                ||
                (g.getGroupType() == GroupType.LOCAL
                && g.getApplicableServer().equalsIgnoreCase(
                        Main.getIns().getServerManager().getServerType()));
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

    public void injectPermission(Player p) {
        PermissionAttachment pA = p.addAttachment(Main.getIns());
        this.playerAttachments.put(p.getUniqueId(), pA);

        PlayerData player = Main.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        Group globalGroup = getGroup(player.getGroupID()), localGroup = getGroup(player.getServerGroup());

        globalGroup.getPermissions().forEach(perm -> pA.setPermission(perm, true));
        if (localGroup != null) {
            localGroup.getPermissions().forEach(perm -> pA.setPermission(perm, true));
        }

        p.recalculatePermissions();
    }

    public void unregisterPermissions(Player p) {
        if (this.playerAttachments.containsKey(p.getUniqueId())) {
            p.removeAttachment(this.playerAttachments.get(p.getUniqueId()));
            this.playerAttachments.remove(p.getUniqueId());
            p.recalculatePermissions();
        }

    }

}
