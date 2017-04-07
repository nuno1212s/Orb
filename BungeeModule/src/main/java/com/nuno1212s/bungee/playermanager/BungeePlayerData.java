package com.nuno1212s.bungee.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Bungee player data class
 */
public class BungeePlayerData extends PlayerData {

    @Setter
    @Getter
    UUID reply;

    public BungeePlayerData(PlayerData d) {
        super(d);
        reply = null;
    }

    @Override
    public Group getRepresentingGroup() {
        return super.getMainGroup();
    }

    @Override
    public void setServerGroup(short groupID, long duration) {

    }

    @Override
    public short getServerGroup() {
        return super.getGroupID();
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }
}
