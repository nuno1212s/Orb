package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.util.Callback;
import lombok.*;

import java.util.UUID;

/**
 * Player data
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public abstract class PlayerData {

    @NonNull
    @Setter
    protected UUID playerID;

    @NonNull
    protected short groupID;

    @NonNull
    @Setter
    protected String playerName;

    @NonNull
    protected long cash;

    @NonNull
    protected long lastLogin;

    @NonNull
    protected boolean premium;

    protected boolean tell;

    public PlayerData(PlayerData coreData) {
        this.playerID = coreData.getPlayerID();
        this.groupID = coreData.getGroupID();
        this.playerName = coreData.getPlayerName();
        this.cash = coreData.getCash();
        this.lastLogin = coreData.getLastLogin();
        this.premium = coreData.isPremium();
        this.tell = coreData.isTell();
    }

    /**
     * All classes that extend Player Data and have their independent server groups
     * should implement this method
     */
    public abstract short getServerGroup();

    /**
     * All classes that extend Player Data should override this method and do their own
     * form of saving player data
     *
     * @param c The callback for when it is done saving
     */
    public abstract void save(Callback c);

    /**
     * Get the main player group
     * @return
     */
    public final Group getMainGroup() {
        return MainData.getIns().getPermissionManager().getGroup(this.groupID);
    }

}
