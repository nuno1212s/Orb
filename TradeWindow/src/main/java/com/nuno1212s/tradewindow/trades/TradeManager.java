package com.nuno1212s.tradewindow.trades;

import com.google.common.collect.ImmutableList;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TradeManager {

    private List<Trade> activeTrades;

    @Getter
    private final List<TradeRequest> tradingRequests;

    @Getter
    private List<UUID> closeExceptions;

    @Getter
    private ItemStack acceptedItem, rejectedItem;

    public TradeManager(Module module) {
        this.activeTrades = new ArrayList<>();
        this.tradingRequests = Collections.synchronizedList(new ArrayList<>());
        this.closeExceptions = new ArrayList<>();

        File configFile = new File(module.getDataFolder(), "config.json");

        if (!configFile.exists()) {

            module.saveResource(configFile, "config.json");

        }

        JSONObject json;

        try (FileReader reader = new FileReader(configFile)) {

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

        this.tradingRequests.add(new TradeRequest(playerRequesting, playerRequested));

    }

    /**
     * Check if a player has a pending trade request
     *
     * @param player
     * @return
     */
    public boolean hasTradeRequest(UUID player) {

        synchronized (this.tradingRequests) {
            for (TradeRequest tradingRequest : this.tradingRequests) {
                if (tradingRequest.getRequestedPlayer().equals(player)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Is there a trade request involving
     *
     * @param playerRequested
     * @param playerResquesting
     * @return
     */
    public boolean hasTradeRequestFrom(UUID playerRequested, UUID playerResquesting) {

        synchronized (this.tradingRequests) {
            for (TradeRequest tradingRequest : this.tradingRequests) {
                if (tradingRequest.getRequestedPlayer().equals(playerRequested) &&
                        tradingRequest.getRequestingPlayer().equals(playerResquesting)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the trade request for the player
     *
     * @param playerRequested
     * @param playerRequesting
     * @return
     */
    public TradeRequest getTradeRequest(UUID playerRequested, UUID playerRequesting) {

        synchronized (this.tradingRequests) {
            for (TradeRequest tradingRequest : this.tradingRequests) {
                if (tradingRequest.getRequestedPlayer().equals(playerRequested) &&
                        tradingRequest.getRequestingPlayer().equals(playerRequesting)) {
                    return tradingRequest;
                }
            }
        }

        return null;
    }

    public TradeRequest getTradeRequest(String requestID) {

        for (TradeRequest tradingRequest : this.tradingRequests) {
            if (tradingRequest.getRequestID().equals(requestID)) {
                return tradingRequest;
            }
        }

        return null;
    }

    /**
     * Destroy a specific trade
     *
     * @param t
     */
    public void destroyTrade(Trade t) {
        this.activeTrades.remove(t);

        t.destroyTrade();
    }

    public void finishTrade(Trade t) {
        this.activeTrades.remove(t);
    }

    /**
     * Get the current active trades
     *
     * @return
     */
    public ImmutableList<Trade> getTrades() {
        return ImmutableList.copyOf(this.activeTrades);
    }

    public void addTrade(Trade trade) {
        this.activeTrades.add(trade);
    }

    public void removeTradeRequest(TradeRequest tradeRequest) {
        this.tradingRequests.remove(tradeRequest);
    }
}
