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

        PermissionAttachment pA = p.addAttachment(BukkitMain.getIns());
        this.playerAttachments.put(p.getUniqueId(), pA);

        Group globalGroup = MainData.getIns().getPermissionManager().getGroup(d.getGroupID());

        List<Short> localGroups = d.getServerGroups();

        Map<String, Boolean> permissions1 = getDirect(pA);

        insertPermission(globalGroup, permissions1);

        if (!localGroups.isEmpty()) {
            for (short s : localGroups) {
                Group localGroup = MainData.getIns().getPermissionManager().getGroup(s);

                insertPermission(localGroup, permissions1);
            }
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

    private void insertPermission(Group group, Map<String, Boolean> permissions) {

        for (String permission : group.getTruePermissions()) {

            permissions.put(permission, true);

        }

    }

    private Field f;

    /**
     * Method to directly update the permissions since the permission attachment
     * .setPermission {@link PermissionAttachment#setPermission(String, boolean)} always calls recalculate and is very expensive to run
     *
     * @param pA
     * @return
     */
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

}
