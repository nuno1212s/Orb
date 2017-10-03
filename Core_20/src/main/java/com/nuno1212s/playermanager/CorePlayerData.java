package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.punishments.Punishment;
import com.nuno1212s.util.Callback;

import java.util.List;
import java.util.UUID;

/**
 * The core data
 */
public class CorePlayerData extends PlayerData {

    public CorePlayerData(UUID playerID, PlayerGroupData groupID, String playerName, long cash, long lastLogin, boolean premium, List<Integer> claimed, Punishment currentPunishment) {
        super(playerID, groupID, playerName, cash, lastLogin, premium, claimed, currentPunishment);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback(null);
        });
    }

    @Override
    public short getServerGroup() {
        return -1;
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        return PlayerGroupData.EXTENSION_RESULT.NEW_GROUP;
    }

    @Override
    public Group getRepresentingGroup() {
        return MainData.getIns().getPermissionManager().getGroup(super.groups.getActiveGroup());
    }
}
