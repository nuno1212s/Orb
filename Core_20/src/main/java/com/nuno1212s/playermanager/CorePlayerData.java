package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.util.Callback;

import java.util.UUID;

/**
 * The core data
 */
public class CorePlayerData extends PlayerData {

    public CorePlayerData(UUID playerID, PlayerGroupData groupID, String playerName, long cash, long lastLogin, boolean premium) {
        super(playerID, groupID, playerName, cash, lastLogin, premium);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }

    @Override
    public short getServerGroup() {
        return -1;
    }

    @Override
    public void setServerGroup(short groupID, long duration) {}

    @Override
    public Group getRepresentingGroup() {
        return MainData.getIns().getPermissionManager().getGroup(super.groups.getActiveGroup());
    }
}
