package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Booster
 */
@Getter
@AllArgsConstructor
public class Booster {

    String boosterID;

    @Nullable
    UUID owner;

    BoosterType type;

    float multiplier;

    long durationInMillis, activationTime;

    boolean activated;

    /**
     * Applicable server refers to the server type, not server name
     * {@link ServerManager#getServerType()}
     */
    String applicableServer;

    String customName;

    public void activate() {
        this.activated = true;
        this.activationTime = System.currentTimeMillis();

        Main.getIns().getRedisHandler().handleBoosterActivation(this);

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getMysqlHandler().updateBooster(this);
        });
    }

    public boolean isExpired() {
        return (activated && this.activationTime + this.durationInMillis <= System.currentTimeMillis());
    }

    /**
     * Is this boosters applicable to the given player, on the current server
     *
     * @param data The player (can be null)
     * @return
     */
    public boolean isApplicable(UUID data) {

        if ((type == BoosterType.PLAYER_GLOBAL || type == BoosterType.PLAYER_SERVER)) {
            if (data == null || owner == null || !owner.equals(data)) {
                return false;
            }
        }

        return (type == BoosterType.GLOBAL_GLOBAL || type == BoosterType.PLAYER_GLOBAL)
                || (MainData.getIns().getServerManager().isApplicable(this.applicableServer) && (type == BoosterType.GLOBAL_SERVER || (type == BoosterType.PLAYER_SERVER)));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Booster && ((Booster) obj).getBoosterID().equalsIgnoreCase(boosterID);
    }
}
