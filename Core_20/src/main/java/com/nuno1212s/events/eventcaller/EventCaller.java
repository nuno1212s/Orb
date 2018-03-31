package com.nuno1212s.events.eventcaller;

import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;

/**
 * Event caller
 */
public interface EventCaller {

    /**
     * Update the player scoreboard information
     */
    void callUpdateInformationEvent(PlayerData args, Object... extra);

    void callGroupUpdateEvent(PlayerData data, Group previousGroup);

    void callRewardsUpdateEvent(PlayerData data);

}
