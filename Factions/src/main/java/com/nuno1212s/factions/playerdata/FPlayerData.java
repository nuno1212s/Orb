package com.nuno1212s.factions.playerdata;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;

public class FPlayerData extends PlayerData {

    private PlayerGroupData serverGroup;

    @Getter
    @Setter
    private long coins, lastDatabaseAccess;

    public FPlayerData(PlayerData original, PlayerGroupData serverGroup, long coins) {
        super(original);
        this.serverGroup = serverGroup;
        this.coins = coins;
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        return serverGroup.setCurrentGroup(groupID, duration);
    }

    @Override
    public short getServerGroup() {
        return serverGroup.getActiveGroup();
    }

    @Override
    public Group getRepresentingGroup() {
        Group mainGroup = this.getMainGroup();

        if (mainGroup.isOverrides()) {
            return mainGroup;
        }

        return MainData.getIns().getPermissionManager().getGroup(this.serverGroup.getActiveGroup());
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);

            c.callback(null);
        });
    }

    public synchronized long getCoins() {
        return coins;
    }

    public synchronized void setCoins(long coins) {
        this.coins = coins;
    }

}
