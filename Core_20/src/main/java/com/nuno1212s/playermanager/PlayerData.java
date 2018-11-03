package com.nuno1212s.playermanager;

import com.nuno1212s.economy.EconomyRedisHandler;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.punishments.Punishment;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.simple.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

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

    protected AtomicLong cash;

    protected long lastLogin;

    protected boolean premium;

    protected boolean autoLogin;

    protected boolean tell;

    private boolean online;

    protected List<Integer> toClaim;

    protected Punishment punishment;

    private boolean shouldSave = true;

    /**
     * Use a weak reference to the player class to avoid any sort of memory leaks
     */
    private WeakReference<Object> playerReference;

    public PlayerData(UUID playerID, PlayerGroupData groups, String playerName, long cash, long lastLogin, boolean premium, boolean autoLogin, List<Integer> toClaim, Punishment punishment) {
        this.playerID = playerID;
        this.groups = groups;
        this.playerName = playerName;
        this.cash = new AtomicLong(cash);
        this.autoLogin = autoLogin;
        this.lastLogin = lastLogin;
        this.premium = premium;
        this.toClaim = toClaim;
        this.punishment = punishment;
    }

    public PlayerData(PlayerData coreData) {
        this.playerID = coreData.getPlayerID();
        this.groups = coreData.getGroups();
        this.playerName = coreData.getPlayerName();
        this.cash = new AtomicLong(coreData.getCash());
        this.lastLogin = coreData.getLastLogin();
        this.premium = coreData.isPremium();
        this.tell = coreData.isTell();
        this.toClaim = coreData.getToClaim();
        this.punishment = coreData.getPunishment();
        this.autoLogin = coreData.isAutoLogin();
    }

    /**
     * Set the main player group
     *
     * @param groupID  The ID of the group to set
     * @param duration The duration of the group (-1 = Permanent)
     */
    public final PlayerGroupData.EXTENSION_RESULT setMainGroup(short groupID, long duration, boolean shoudUseRedis) {
        Group previous = MainData.getIns().getPermissionManager().getGroup(this.groups.getActiveGroup());

        PlayerGroupData.EXTENSION_RESULT extension_result = this.groups.setCurrentGroup(groupID, duration);

        if (MainData.getIns().getEventCaller() != null) {
            MainData.getIns().getEventCaller().callGroupUpdateEvent(this, previous);

            /**
             * Calling the update information event causes the player permissions to be updated by
             *
             * {@link com.nuno1212s.events.listeners.InformationUpdateListener#onUpdate(PlayerInformationUpdateEvent)}
             */

            MainData.getIns().getEventCaller().callUpdateInformationEvent(this, PlayerInformationUpdateEvent.Reason.GROUP_UPDATE);
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
     * <p>
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
     * <p>
     * If you only need the ID of the group {@link #getGroupID()} should be used
     *
     * @return The main player group
     */
    public final Group getMainGroup() {
        return MainData.getIns().getPermissionManager().getGroup(this.groups.getActiveGroup());
    }

    public List<Short> getServerGroups() {
        return Collections.singletonList(getServerGroup());
    }

    /**
     * All classes that extend Player Data and have their independent server groups
     * should implement this method
     */
    public abstract short getServerGroup();

    public boolean hasPermission(String permission) {

        if (getRepresentingGroup().hasPermission(permission)) {
            return true;
        }

        for (Short groupID : getServerGroups()) {
            Group g = MainData.getIns().getPermissionManager().getGroup(groupID);

            if (g == null) {
                continue;
            }

            if (g.hasPermission(permission)) return true;

        }

        return false;
    }

    /**
     * All classes that extend Player Data should override this method and do their own
     * form of saving player data
     * <p>
     * The {@link Callback#callback(Object)} should be called when the player data is finished saving
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
     * <p>
     * THIS DOES NOT AUTO CHECK SERVER GROUPS!
     * You can however Override this method to also check the server groups at the same time
     * and avoid creating another timer
     *
     * @param p The player instance
     */
    public void checkExpiration(PlayerData p) {
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
    public final long getCash() {
        return this.cash.get();
    }

    /**
     * Set the players cash
     * <p>
     * AUTO CALLS PLAYER INFORMATION EVENT
     *
     * @param cash
     */
    public final void setCash(long cash, boolean shouldUseRedis) {
        this.cash.set(cash);


        if (MainData.getIns().getEventCaller() != null && isPlayerOnServer()) {
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this);
        } else if (!isPlayerOnServer() && shouldUseRedis) {
            MainData.getIns().getPlayerManager().getEconomyRedisHandler().sendCashUpdate(getPlayerID(), cash, EconomyRedisHandler.Operation.SET);
        }
    }

    /**
     * Thread safe addCash command
     *
     * @param cash
     */
    public final void addCash(long cash) {
        addCash(cash, true);
    }

    public final void addCash(long cash, boolean useRedis) {
        this.cash.addAndGet(cash);

        if (this.isPlayerOnServer())
            MainData.getIns().getEventCaller().callUpdateInformationEvent(this, PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);
        else if (useRedis)
            MainData.getIns().getPlayerManager().getEconomyRedisHandler().sendCashUpdate(getPlayerID(), cash,
                    EconomyRedisHandler.Operation.ADD);
    }

    /**
     * Thread safe remove cash command
     *
     * @param cash
     * @return
     */
    public final boolean removeCash(long cash) {
        return removeCash(cash, true);
    }

    public final boolean removeCash(long cash, boolean useRedis) {

        while (true) {

            long currentCash = this.cash.get();

            if (currentCash >= cash) {
                if (this.cash.compareAndSet(currentCash, currentCash - cash)) {

                    if (this.isPlayerOnServer())
                        MainData.getIns().getEventCaller().callUpdateInformationEvent(this, PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);
                    else if (useRedis)
                        MainData.getIns().getPlayerManager().getEconomyRedisHandler().sendCashUpdate(getPlayerID(), cash,
                                EconomyRedisHandler.Operation.REMOVE);

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public final void setCash(long cash) {
        this.setCash(cash, true);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerData && ((PlayerData) obj).getPlayerID().equals(this.getPlayerID());
    }

    @Override
    public int hashCode() {
        return this.getPlayerID().hashCode();
    }

    /**
     * Has the player claimed a reward
     * <p>
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
     * <p>
     * {@link com.nuno1212s.rewards.Reward}
     *
     * @param id The ID of the reward
     */
    public final void claim(int id) {
        if (this.toClaim.contains(id)) {
            toClaim.remove((Integer) id);
            MainData.getIns().getEventCaller().callRewardsUpdateEvent(this);
        }
    }

    /**
     * Add a reward to be claimed
     *
     * @param id The ID of the reward
     */
    public final void addToClaim(int id) {
        toClaim.add(id);
        MainData.getIns().getEventCaller().callRewardsUpdateEvent(this);
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

    /**
     * Set the player reference
     *
     * @param playerReference
     */
    public void setPlayerReference(Object playerReference) {
        this.playerReference = new WeakReference<>(playerReference);
    }

    /**
     * Get the player reference
     *
     * @param playerType The type of player stored
     * @param <T>
     * @return
     */
    public <T> T getPlayerReference(Class<T> playerType) {
        if (this.playerReference == null) {
            return null;
        }

        if (this.playerReference.get() == null) {
            return null;
        }

        return playerType.cast(this.playerReference.get());
    }

    /**
     * Check if the player is currently online on the server (Only this server instance, player can be online on another server of the network
     *
     * @return
     */
    public boolean isPlayerOnServer() {
        return this.playerReference != null && this.playerReference.get() != null;
    }

}