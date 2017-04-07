package com.nuno1212s.permissionmanager.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
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

    public PlayerGroupData(String groups) {
        this.groups = new ArrayList<>();

        String[] groupList = groups.split("!");

        for (String groupS : groupList) {
            this.groups.add(new PlayerGroup(groupS));
        }

    }

    public String toDatabase() {
        StringBuilder builder = new StringBuilder();
        this.groups.forEach(group -> {
            builder.append(group.toDatabase());
            builder.append("!");
        });
        return builder.toString();
    }

    public short getActiveGroup() {
        for (PlayerGroup group : this.groups) {
            if (group.isActive()) {
                return group.getGroupID();
            }
        }
        return 0;
    }

    public PlayerGroup getActiveGroupIns() {
        for (PlayerGroup group : this.groups) {
            if (group.isActive()) {
                return group;
            }
        }
        return null;
    }

    public void setCurrentGroup(short groupID, long duration) {
        PlayerGroup g = new PlayerGroup(groupID, -1, duration);

        PlayerGroup group = getActiveGroupIns();

        if (checkIfExistsAndExtend(groupID, duration)) {
            return;
        }

        System.out.println("Added new group " + g.getGroupID());

        group.deactivate();

        g.activate();
        this.groups.add(0, g);

        if (g.isPermanent()) {
            //Because the group will never expire, we don't have to remember the rest of the groups to return to
            this.groups = this.groups.subList(0, 1);
        }

        if (this.groups.size() > MainData.getIns().getPermissionManager().getMaxGroupsPerPlayer()) {
            //TODO: REMOVE A GROUP.
            deleteGroup();
        }

    }

    private boolean checkIfExistsAndExtend(short groupID, long duration) {
        Iterator<PlayerGroup> groupIterator = this.groups.iterator();

        PlayerGroup toActivate = null;

        //TODO: SEND MESSAGES
        while (groupIterator.hasNext()) {
            PlayerGroup group = groupIterator.next();

            if (group.getGroupID() == groupID) {
                toActivate = group;
                toActivate.extendDuration(duration);
                if (group.isActive()) {
                    return true;
                }
                groupIterator.remove();
                break;
            }

        }


        if (toActivate != null) {
            this.groups.get(0).deactivate();
            toActivate.activate();
            this.groups.add(0, toActivate);
            return true;
        }
        return false;
    }

    public void checkExpiration(Player p) {

        boolean expired = false;

        PlayerGroup activeGroupIns = this.getActiveGroupIns();
        if (activeGroupIns.isActive()) {

            long timeLeft = activeGroupIns.getDuration();
            long timeGroupEnd = timeLeft == Long.MAX_VALUE ? Long.MAX_VALUE : activeGroupIns.getActivationTime() + timeLeft;

            if (System.currentTimeMillis() > timeGroupEnd) {
                //END CURRENT GROUP

                this.groups.remove(activeGroupIns);

                Group group = MainData.getIns().getPermissionManager().getGroup(activeGroupIns.getGroupID());

                MainData.getIns().getMessageManager().getMessage("GROUP_EXPIRED")
                        .format("%group%", group.getGroupPrefix()).sendTo(p);
                expired = true;
            }
        }

        if (expired) {
            PlayerGroup nextGroup = this.getNextGroup();

            nextGroup.activate();

            Group g = MainData.getIns().getPermissionManager().getGroup(nextGroup.getGroupID());

            if (nextGroup.isPermanent()) {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED")
                        .format("%newGroup%", g.getGroupPrefix()).sendTo(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("GROUP_CHANGED_TIME")
                        .format("%newGroup%", g.getGroupPrefix()).format("%time%", new TimeUtil("MM:DD:MM:SS").toTime(nextGroup.getActivationTime()))
                        .sendTo(p);
            }

        }
    }

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

    private PlayerGroup getNextGroup() {
        return this.groups.size() == 1 ? this.getActiveGroupIns() : this.groups.get(1);
    }

    public void sendTo(Player p) {
        this.groups.forEach(group -> {
            p.sendMessage("");
            p.sendMessage(String.valueOf(group.getGroupID()));
            p.sendMessage(String.valueOf(group.getActivationTime()));
        });
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