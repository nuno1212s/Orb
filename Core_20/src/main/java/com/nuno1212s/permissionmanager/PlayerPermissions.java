package com.nuno1212s.permissionmanager;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Permissions for bukkit version of core
 */
public class PlayerPermissions {

    private Map<UUID, PermissionAttachment> playerAttachments;

    public PlayerPermissions() {
        playerAttachments = new HashMap<>();
    }

    public void injectPermission(Player p, PlayerData d) {

        long start = System.currentTimeMillis();
        PermissionAttachment pA = p.addAttachment(BukkitMain.getIns());
        this.playerAttachments.put(p.getUniqueId(), pA);

        Group globalGroup = MainData.getIns().getPermissionManager().getGroup(d.getGroupID()),
                localGroup = MainData.getIns().getPermissionManager().getGroup(d.getServerGroup());

        globalGroup.getPermissions().forEach(perm -> pA.setPermission(perm, true));
        if (localGroup != null) {
            List<String> permissions = localGroup.getPermissions();
            Map<String, Boolean> permissions1 = getDirect(pA);
            permissions.forEach(perm -> {
                permissions1.put(perm, true);
            });
        }

        p.recalculatePermissions();
    }

    private Field f;

    private Map<String, Boolean> getDirect(PermissionAttachment pA) {

        try {
            if (f == null) {
                f = pA.getClass().getDeclaredField("permissions");
                f.setAccessible(true);
            }


            return (Map<String, Boolean>) f.get(pA);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new LinkedHashMap<>();
    }

    public void unregisterPermissions(Player p) {
        if (this.playerAttachments.containsKey(p.getUniqueId())) {
            p.removeAttachment(this.playerAttachments.get(p.getUniqueId()));

            this.playerAttachments.remove(p.getUniqueId());

            p.recalculatePermissions();
        }

    }

}
