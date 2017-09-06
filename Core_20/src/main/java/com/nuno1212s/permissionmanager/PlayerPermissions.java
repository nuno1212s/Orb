package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Permissions for bukkit version of core
 */
public class PlayerPermissions {

    private Map<UUID, PermissionAttachment> playerAttachments;

    public PlayerPermissions() {
        playerAttachments = new HashMap<>();
    }

    public void injectPermission(Player p, PlayerData d) {
        PermissionAttachment pA = p.addAttachment(BukkitMain.getIns());
        this.playerAttachments.put(p.getUniqueId(), pA);

        Group globalGroup = MainData.getIns().getPermissionManager().getGroup(d.getGroupID()),
                localGroup = MainData.getIns().getPermissionManager().getGroup(d.getServerGroup());

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
