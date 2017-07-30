package com.nuno1212s.spawners.playerdata;

import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Player data class
 */
@AllArgsConstructor
public class PlayerData {

    @Getter
    private UUID playerID;

    private long lastKill, earnedCoins, lastMessage;

    private int kills;

    private double multiplier;

    public PlayerData(UUID playerID, double multiplier) {
        this.playerID = playerID;
        this.multiplier = multiplier;
        this.earnedCoins = 0;
        this.lastMessage = 0;
        this.lastKill = 0;
    }

    /**
     * If the last kill the player made was more than 10 seconds ago, discard the player info
     *
     * @return
     */
    public boolean shouldDiscard() {
        return System.currentTimeMillis() - lastKill >= 10000;
    }

    /**
     * Only send the player a message every 8 seconds, to prevent spam
     *
     * @return
     */
    public boolean shouldMessage() {
        return System.currentTimeMillis() - lastMessage >= 8000;
    }

    public void discard() {
        if (earnedCoins != 0) {
            sendMessage();
        }
    }

    public void addKill(long coins) {
        earnedCoins += coins;
        lastKill = System.currentTimeMillis();
        kills++;
    }

    public void sendMessage() {
        lastMessage = System.currentTimeMillis();

        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return;
        }

        MainData.getIns().getMessageManager().getMessage("KILLED_ENTITIES")
                .format("%coins%", String.valueOf(earnedCoins))
                .format("%multiplier%", String.valueOf(multiplier))
                .format("%kills%", String.valueOf(kills))
                .sendTo(player);
        earnedCoins = 0;
        kills = 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerData && ((PlayerData) obj).getPlayerID().equals(getPlayerID());
    }
}
