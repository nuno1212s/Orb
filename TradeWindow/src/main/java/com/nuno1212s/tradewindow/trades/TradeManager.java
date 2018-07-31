package com.nuno1212s.tradewindow.trades;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TradeManager {

    private List<Trade> activeTrades;

    private Map<UUID, UUID> tradingRequests;

    @Getter
    private ItemStack acceptedItem, rejectedItem;

    public TradeManager(Module module) {
        this.activeTrades = new ArrayList<>();
        this.tradingRequests = new HashMap<>();

        File configFile = new File(module.getDataFolder(), "config.json");

        if (!configFile.exists()) {

            module.saveResource(configFile, "config.json");

        }

        JSONObject json;

        try (FileReader reader = new FileReader(configFile)){

            json = (JSONObject) new JSONParser().parse(reader);

        } catch (IOException | ParseException e) {
            e.printStackTrace();

            return;
        }

        this.acceptedItem = new SerializableItem((JSONObject) json.get("AcceptedItem"));
        this.rejectedItem = new SerializableItem((JSONObject) json.get("RejectedItem"));
    }

    /**
     * Get a trade where the player is participating
     *
     * @param tradePlayer
     * @return
     */
    public Trade getTrade(UUID tradePlayer) {
        for (Trade activeTrade : this.activeTrades) {
            if (activeTrade.getPlayer1().equals(tradePlayer) || activeTrade.getPlayer2().equals(tradePlayer)) {
                return activeTrade;
            }
        }

        return null;
    }

    /**
     * Checking if a player is participating in a trade
     *
     * @param player
     * @return
     */
    public boolean isParticipatingInTrade(UUID player) {
        return getTrade(player) != null;
    }

    /**
     * Registers requests
     *
     * @param playerRequesting
     * @param playerRequested
     */
    public void registerTradeRequest(UUID playerRequesting, UUID playerRequested) {

        this.tradingRequests.put(playerRequested, playerRequesting);

    }

    /**
     * Check if a player has a pending trade request
     *
     * @param player
     * @return
     */
    public boolean hasTradeRequest(UUID player) {
        return this.tradingRequests.containsKey(player);
    }

}
