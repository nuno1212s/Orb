package com.nuno1212s.rewards;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Handles the master reward class
 */
@ToString
public abstract class Reward {

    @Getter
    @Setter
    protected int id;

    @Getter
    protected RewardType type;

    @Getter
    protected String serverType;

    @Getter
    protected boolean isDefault;

    public Reward(int id, RewardType type, String serverType, boolean isDefault) {
        this.id = id;
        this.type = type;
        this.serverType = serverType;
        this.isDefault = isDefault;
    }

    public static enum RewardType {

        MESSAGE,
        ITEM,
        CASH,
        SV_CRRCY;

    }

}
