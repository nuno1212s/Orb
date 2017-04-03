package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.Main;
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

    public void injectPermission(Player p) {
        PermissionAttachment pA = p.addAttachment(Main.getIns());
        this.playerAttachments.put(p.getUniqueId(), pA);

        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        Group globalGroup = MainData.getIns().getPermissionManager().getGroup(player.getGroupID()),
                localGroup = MainData.getIns().getPermissionManager().getGroup(player.getServerGroup());

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
