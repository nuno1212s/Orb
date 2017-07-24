package com.nuno1212s.npcinbox.rewards;

import com.nuno1212s.npcinbox.playermanager.NPCPlayer;
import org.bukkit.entity.Player;

/**
 * Rewards
 */
public class Reward {

    private final int id;

    private RewardType type;

    private Object o;

    public Reward(int id, RewardType type, Object o) {
        this.id = id;
        this.type = type;
        this.o = o;
    }

    public void deliver(Player p, NPCPlayer d) {

    }

    public static enum RewardType {

        MESSAGE,
        ITEM,
        CASH,
        SV_CRRCY;

    }

}
