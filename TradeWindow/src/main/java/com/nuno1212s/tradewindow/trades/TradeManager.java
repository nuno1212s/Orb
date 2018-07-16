package com.nuno1212s.tradewindow.trades;

import java.util.*;

public class TradeManager {

    private List<Trade> activeTrades;

    private Map<UUID, UUID> tradingRequests;

    public TradeManager() {
        this.activeTrades = new ArrayList<>();
        this.tradingRequests = new HashMap<>();
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
