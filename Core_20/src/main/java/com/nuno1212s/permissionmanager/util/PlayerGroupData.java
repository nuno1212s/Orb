package com.nuno1212s.permissionmanager.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles the timed groups
 */
public class PlayerGroupData {

    private List<PlayerGroup> groups;

    public PlayerGroupData() {
        groups = new ArrayList<>(MainData.getIns().getPermissionManager().getMaxGroupsPerPlayer());
        short groupID = MainData.getIns().getPermissionManager().getDefaultGroup().getGroupID();
        groups.add(new PlayerGroup(groupID, System.currentTimeMillis(), -1/*PERMANENT DURATION*/));
    }

    public PlayerGroupData(short startingGroup) {
        groups = new ArrayList<>(MainData.getIns().getPermissionManager().getMaxGroupsPerPlayer());
        groups.add(new PlayerGroup(startingGroup, System.currentTimeMillis(), -1/*PERMANENT DURATION*/));
    }

    public PlayerGroupData(String groups) {
        this.groups = new ArrayList<>();

        String[] groupList = groups.split("!");

        for (String groupS : groupList) {
            this.groups.add(new PlayerGroup(groupS));
        }

    }

    /**
     *
     * @return
     */
    public String toDatabase() {
        StringBuilder builder = new StringBuilder();
        this.groups.forEach(group -> {
            builder.append(group.toDatabase());
            builder.append("!");
        });
        return builder.toString();
    }

    /**
     * Get the group ID of the current active group
     * @return
     */
    public short getActiveGroup() {
        for (PlayerGroup group : this.groups) {
            if (group.isActive()) {
                return group.getGroupID();
            }
        }
        return 0;
    }

    /**
     * Get the Player Group instance of the current active group
     * @return
     */
    public PlayerGroup getActiveGroupIns() {
        for (PlayerGroup group : this.groups) {
            if (group.isActive()) {
                return group;
            }
        }
        return null;
    }

    /**
     * Sets the current player group
     *
     * @param groupID The ID of the group
     * @param duration The duration of the group
     * @return The result of the change
     */
    public EXTENSION_RESULT setCurrentGroup(short groupID, long duration) {
        PlayerGroup g = new PlayerGroup(groupID, -1, duration);

        PlayerGroup group = getActiveGroupIns();

        EXTENSION_RESULT extension = checkIfExistsAndExtend(groupID, duration);

        if (extension == EXTENSION_RESULT.EXTENDED_CURRENT) {
            return extension;
        } else if (extension == EXTENSION_RESULT.EXTENDED_AND_ACTIVATED) {
            return extension;
        }

        group.deactivate();

        g.activate();
        this.groups.add(0, g);

        if (g.isPermanent()) {
            //Because the group will never expire, we don't have to remember the rest of the groups to return to
            this.groups = this.groups.subList(0, 1);
        }

        if (this.groups.size() > MainData.getIns().getPermissionManager().getMaxGroupsPerPlayer()) {
            deleteGroup();
        }

        return extension;
    }

    private EXTENSION_RESULT checkIfExistsAndExtend(short groupID, long duration) {
        Iterator<PlayerGroup> groupIterator = this.groups.iterator();

        PlayerGroup toActivate = null;

        //TODO: SEND MESSAGES
        while (groupIterator.hasNext()) {
            PlayerGroup group = groupIterator.next();

            /*If the group is already in the players group list
            * it should be extended
            * If the group is not currently active, it should also be activated
            */
            if (group.getGroupID() == groupID) {
                toActivate = group;
                toActivate.extendDuration(duration);
                if (group.isActive()) {
                    //If the group is active, no need to remove it and re-add it as the first group
                    return EXTENSION_RESULT.EXTENDED_CURRENT;
                }
                groupIterator.remove();
                break;
            }

        }

        if (toActivate != null) {
            this.groups.get(0).deactivate();
            toActivate.activate();
            this.groups.add(0, toActivate);
            return EXTENSION_RESULT.EXTENDED_AND_ACTIVATED;
        }

        return EXTENSION_RESULT.NEW_GROUP;
    }

    /**
     *
     * @param p
     */
    public void checkExpiration(PlayerData p) {

        boolean expired = false;

        PlayerGroup activeGroupIns = this.getActiveGroupIns();
        if (activeGroupIns == null) {
            this.groups.get(0).activate();
            return;
        }

        if (activeGroupIns.isActive()) {
            if (activeGroupIns.isPermanent()) {
                return;
            }
            long timeLeft = activeGroupIns.getDuration();
            long timeGroupEnd = timeLeft == Long.MAX_VALUE ? Long.MAX_VALUE : activeGroupIns.getActivationTime() + timeLeft;

            if (System.currentTimeMillis() > timeGroupEnd) {
                //END CURRENT GROUP

                System.out.println(this.groups);
                this.groups.remove(activeGroupIns);
                System.out.println(this.groups);

                Group group = MainData.getIns().getPermissionManager().getGroup(activeGroupIns.getGroupID());

                MainData.getIns().getMessageManager().getMessage("GROUP_EXPIRED")
                        .format("%group%", group.getGroupPrefix())
                        .sendTo(p);
                expired = true;
            }
        }

        if (expired) {
            PlayerGroup nextGroup = this.groups.get(0);

            System.out.println(nextGroup);

            if (nextGroup == null) {
                nextGroup = new PlayerGroup(MainData.getIns().getPermissionManager().getDefaultGroup().getGroupID(), -1, -1);
            }

            nextGroup.activate();

            Group g = MainData.getIns().getPermissionManager().getGroup(nextGroup.getGroupID());

            if (nextGroup.isPermanent()) {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED_EXPIRED")
                        .format("%newGroup%", g.getGroupPrefix())
                        .sendTo(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED_EXPIRED_TIME")
                        .format("%newGroup%", g.getGroupPrefix())
                        .format("%time%", new TimeUtil("MM:DD:MM:SS").toTime(nextGroup.getActivationTime()))
                        .sendTo(p);
            }

            MainData.getIns().getEventCaller().callGroupUpdateEvent(p, MainData.getIns().getPermissionManager().getGroup(activeGroupIns.getGroupID()));
            MainData.getIns().getEventCaller().callUpdateInformationEvent(p);
        }

    }

    /**
     * Delete a group to make space for a new group
     *
     * Deletes the oldest groups and makes sure there is always at least one permanent group to fallback on
     */
    private void deleteGroup() {
        //Check to see if there are no permanent groups
        int currentIt = 0;
        for (PlayerGroup group : this.groups) {
            if (group.isPermanent()) {

                if (currentIt + 2 >= this.groups.size()) {
                    continue;
                }

                this.groups = this.groups.subList(0, currentIt + 1);

                return;
            }
            currentIt++;
        }

        List<PlayerGroup> newGroups = new ArrayList<>();

        PlayerGroup permanentGroup = new PlayerGroup(MainData.getIns().getPermissionManager().getDefaultGroup().getGroupID(), -1, -1);

        for (PlayerGroup group : this.groups) {
            if (group.isPermanent()) {
                permanentGroup = group;
            }
        }

        newGroups.add(permanentGroup);

        for (int i = 4; i > 0; i--) {
            newGroups.add(0, this.groups.get(i));
        }

        this.groups = newGroups;

    }

    /**
     * Get the next group to be activated
     * @return
     */
    private PlayerGroup getNextGroup() {
        return this.groups.size() == 1 ? this.getActiveGroupIns() : this.groups.get(1);
    }

    /**
     * Send the player groups to a player
     *
     * @param p
     */
    public void sendTo(Player p) {
        this.groups.forEach(group -> {
            p.sendMessage("");
            p.sendMessage(String.valueOf(group.getGroupID()));
            p.sendMessage(String.valueOf(group.getActivationTime()));
        });
    }

    public enum EXTENSION_RESULT {
        EXTENDED_CURRENT,
        EXTENDED_AND_ACTIVATED,
        NEW_GROUP
    }

}

@AllArgsConstructor
@Getter
@ToString
class PlayerGroup {

    short groupID;

    long activationTime, duration;

    PlayerGroup(String groupInfo) {
        String[] group = groupInfo.split("\\.");
        this.groupID = Short.parseShort(group[0]);
        this.activationTime = Long.parseLong(group[1]);
        this.duration = Long.parseLong(group[2]);
    }

    boolean isActive() {
        return this.activationTime > 0;
    }

    void activate() {
        this.activationTime = System.currentTimeMillis();
    }

    boolean isPermanent() {
        return duration < 0;
    }

    void deactivate() {
        if (duration > 0) {
            this.duration = (getDuration() + getActivationTime()) - System.currentTimeMillis();
        }
        this.activationTime = -1;
    }

    void extendDuration(long durationToExtend) {
        if (isPermanent()) {
            return;
        }
        this.duration += durationToExtend;
    }

    String toDatabase() {
        return String.valueOf(groupID) + "." + String.valueOf(activationTime) + "." + String.valueOf(duration);
    }

}