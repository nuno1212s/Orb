package com.nuno1212s.hub.playerdata;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;

/**
 * Player Data
 */
public class HPlayerData extends PlayerData {

    @Getter
    @Setter
    private boolean chatEnabled = true, playerShown = false;

    public HPlayerData(PlayerData d, boolean chatEnabled, boolean playerShown) {
        super(d);
        this.chatEnabled = chatEnabled;
        this.playerShown = playerShown;
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        return setMainGroup(groupID, duration);
    }

    @Override
    public short getServerGroup() {
        return getGroupID();
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            Main.getIns().getMySqlManager().savePlayerData(this);
            c.callback(null);
        });
    }

    @Override
    public Group getRepresentingGroup() {
        return getMainGroup();
    }
}
