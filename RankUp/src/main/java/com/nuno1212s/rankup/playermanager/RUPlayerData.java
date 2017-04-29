package com.nuno1212s.rankup.playermanager;

import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;

/**
 * The Player Data for the full pvp server
 */
@Getter
@Setter
public class RUPlayerData extends PlayerData {

    PlayerGroupData groupData;

    long lastDatabaseAccess;

    volatile long coins;

    public RUPlayerData(PlayerData d) {
        super(d);
        this.coins = 0;
        this.groupData = new PlayerGroupData();
    }

    public synchronized final void setCoins(long coins) {
        this.coins = coins;
    }

    public synchronized final long getCoins() {
        return this.coins;
    }

    @Override
    public Group getRepresentingGroup() {
        Group mainGroup = super.getMainGroup();
        if (mainGroup.isOverrides()) {
            return mainGroup;
        }
        return MainData.getIns().getPermissionManager().getGroup(this.groupData.getActiveGroup());
    }

    @Override
    public short getServerGroup() {
        return this.groupData.getActiveGroup();
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        return this.groupData.setCurrentGroup(groupID, duration);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            Main.getIns().getMysql().savePlayerData(this);
            c.callback();
        });
    }
}
