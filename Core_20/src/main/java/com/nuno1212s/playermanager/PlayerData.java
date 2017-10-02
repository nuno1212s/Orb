package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.punishments.Punishment;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.util.Callback;
import lombok.*;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * Player data
 */
@Getter
@Setter
@ToString
public abstract class PlayerData {

    protected final UUID playerID;

    protected PlayerGroupData groups;

    protected String playerName;

    protected long cash;

    protected long lastLogin;

    protected boolean premium;

    protected boolean tell;

    protected List<Integer> toClaim;

    protected Punishment punishment;

    private boolean shouldSave = true;

    public PlayerData(UUID playerID, PlayerGroupData groups, String playerName, long cash, long lastLogin, boolean premium, List<Integer> toClaim, Punishment punishment) {
        this.playerID = playerID;
        this.groups = groups;
        this.playerName = playerName;
        this.cash = cash;
        this.lastLogin = lastLogin;
        this.premium = premium;
        this.toClaim = toClaim;
        this.punishment = punishment;
    }

    public PlayerData(PlayerData coreData) {
        this.playerID = coreData.getPlayerID();
        this.groups = coreData.getGroups();
        this.playerName = coreData.getPlayerName();
        this.cash = coreData.getCash();
        this.lastLogin = coreData.getLastLogin();
        this.premium = coreData.isPremium();
        this.tell = coreData.isTell();
        this.toClaim = coreData.getToClaim();
        this.punishment = coreData.getPunishment();
    }

    /**
     * Set the main player group
     *
     * @param groupID The ID of the group to set
     * @param duration The duration of the group (-1 = Permanent)
     */
    public final PlayerGroupData.EXTENSION_RESULT setMainGroup(short groupID, long duration, boolean shoudUseRedis) {
        PlayerGroupData.EXTENSION_RESULT extension_result = this.groups.setCurrentGroup(groupID, duration);
        if (MainData.getIns().getEventCaller() != null) {
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this);
        }

        if (shoudUseRedis) {
            JSONObject obj = new JSONObject();
            obj.put("PlayerID", this.getPlayerID().toString());
            obj.put("GroupID", groupID);
            obj.put("Duration", duration);

            MainData.getIns().getRedisHandler().sendMessage(new Message("BUNGEE", "GROUPUPDATE", obj).toByteArray());
        }

        return extension_result;
    }

    public final PlayerGroupData.EXTENSION_RESULT setMainGroup(short groupID, long duration) {
        return this.setMainGroup(groupID, duration, true);
    }

    /**
     * Get the ID of the main player group
     *
     * {@link #getMainGroup()} Has a similar outcome, except it auto fetches the group from the group lists,
     * If you only need the group ID, this method is less expensive
     *
     * @return
     */
    public short getGroupID() {
        return this.groups.getActiveGroup();
    }

    /**
     * Get the main player group
     *
     * If you only need the ID of the group {@link #getGroupID()} should be used
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
     * The {@link Callback#callback(Object...)} should be called when the player data is finished saving
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

    /**
     * Check if the player's global group has expired.
     *
     * THIS DOES NOT AUTO CHECK SERVER GROUPS!
     * You can however Override this method to also check the server groups at the same time
     * and avoid creating another timer
     *
     * @param p The player instance
     */
    public void checkExpiration(Player p) {
        this.groups.checkExpiration(p);
    }

    /**
     * Get the player name with the players group prefix already added
     *
     * @return
     */
    public String getNameWithPrefix() {
        return this.getRepresentingGroup().getGroupPrefix() + this.getPlayerName();
    }

    /**
     * Get the players cash balance
     *
     * @return The players cash balance
     */
    public synchronized final long getCash() {
        return this.cash;
    }

    /**
     * Set the players cash
     *
     * AUTO CALLS PLAYER INFORMATION EVENT
     *
     * @param cash
     */
    public synchronized final void setCash(long cash, boolean shouldUseRedis) {
        this.cash = cash;

        if (shouldUseRedis) {
            MainData.getIns().getRedisHandler().sendMessage(new byte[0]);
        }

        if (MainData.getIns().getEventCaller() != null) {
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this);
        }
    }

    public synchronized final void setCash(long cash) {
        this.setCash(cash, true);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerData && ((PlayerData) obj).getPlayerID().equals(this.getPlayerID());
    }

    /**
     * Has the player claimed a reward
     *
     * {@link com.nuno1212s.rewards.Reward}
     *
     * @param id The ID of the reward
     * @return True if the player has claimed, false if not
     */
    public final boolean hasClaimed(int id) {
        return !this.toClaim.contains(id);
    }

    /**
     * Claim a reward for the player
     *
     * {@link com.nuno1212s.rewards.Reward}
     *
     * @param id The ID of the reward
     */
    public final void claim(int id) {
        if (this.toClaim.contains(id)) {
            toClaim.remove((Integer) id);
        }
    }

    /**
     * Add a reward to be claimed
     *
     * @param id The ID of the reward
     */
    public final void addToClaim(int id) {
        toClaim.add(id);
    }

    /**
     * Transform this players unclaimed rewards to a serialized string
     *
     * @return
     */
    public final String getToClaimToString() {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (Integer integer : this.toClaim) {
            if (isFirst) {
                builder.append(integer);
                isFirst = false;
                continue;
            }
            builder.append(",");
            builder.append(integer);
        }
        return builder.toString();
    }
}