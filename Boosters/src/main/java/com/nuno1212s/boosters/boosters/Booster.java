package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
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

    String applicableServer;

    String customName;

    public void activate() {
        this.activated = true;
        this.activationTime = System.currentTimeMillis();

        Main.getIns().getRedisHandler().handleBoosterActivation(this);

    }

    public boolean isExpired() {
        return (activated && this.activationTime + this.durationInMillis <= System.currentTimeMillis());
    }

    public boolean isApplicable(UUID data) {

        if (data != null && type != BoosterType.GLOBAL_GLOBAL && type != BoosterType.GLOBAL_SERVER) {
            if (owner != null && !owner.equals(data)) {
                return false;
            }
        }

        String serverType = MainData.getIns().getServerManager().getServerType();
        return (type == BoosterType.GLOBAL_GLOBAL || type == BoosterType.PLAYER_GLOBAL)
                || (serverType.equalsIgnoreCase(applicableServer) && (type == BoosterType.GLOBAL_SERVER || type == BoosterType.PLAYER_SERVER));
    }

}
