package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.util.Callback;
import lombok.*;
import org.bukkit.entity.Player;

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
    protected UUID playerID;

    @NonNull
    protected PlayerGroupData groups;

    @NonNull
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
        this.groups = coreData.getGroups();
        this.playerName = coreData.getPlayerName();
        this.cash = coreData.getCash();
        this.lastLogin = coreData.getLastLogin();
        this.premium = coreData.isPremium();
        this.tell = coreData.isTell();
    }

    /**
     * Set the main player group
     *
     * @param groupID The ID of the group to set
     * @param duration The duration of the group (-1 = Permanent)
     */
    public PlayerGroupData.EXTENSION_RESULT setMainGroup(short groupID, long duration) {
        PlayerGroupData.EXTENSION_RESULT extension_result = this.groups.setCurrentGroup(groupID, duration);
        if (MainData.getIns().getEventCaller() != null) {
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this);
        }
        return extension_result;
    }

    public short getGroupID() {
        return this.groups.getActiveGroup();
    }

    /**
     * Get the main player group
     *
     * @return The main player group
     */
    public final Group getMainGroup() {
        return MainData.getIns().getPermissionManager().getGroup(this.groups.getActiveGroup());
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
     * Set the server group
     *
     * @param groupID the ID of the group
     */
    public abstract PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration);

    /**
     * Get the group to be displayed on the scoreboard and the chat
     *
     * @return The group that represents the player
     */
    public abstract Group getRepresentingGroup();

    public void checkExpiration(Player p) {
        this.groups.checkExpiration(p);
    }

    public String getNameWithPrefix() {
        return this.getRepresentingGroup().getGroupPrefix() + this.getPlayerName();
    }

    public synchronized final long getCash() {
        return this.cash;
    }

    public synchronized final void setCash(long cash) {
        this.cash = cash;
        MainData.getIns().getRedisHandler().sendMessage("");
        if (MainData.getIns().getEventCaller() != null) {
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerData && ((PlayerData) obj).getPlayerID().equals(this.getPlayerID());
    }
}
