package com.nuno1212s.fullpvp.playermanager;

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
public class PVPPlayerData extends PlayerData {

    PlayerGroupData groupData;

    long coins;

    public PVPPlayerData(PlayerData d) {
        super(d);
    }

    @Override
    public Group getRepresentingGroup() {
        return null;
    }

    @Override
    public short getServerGroup() {
        return this.groupData.getActiveGroup();
    }

    @Override
    public void setServerGroup(short groupID, long duration) {
        this.groupData.setCurrentGroup(groupID, duration);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }
}
