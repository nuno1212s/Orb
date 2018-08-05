package com.nuno1212s.tradewindow.trades;

import lombok.Getter;

import java.util.UUID;

public class TradeRequest {

    private static int REQUEST_TIME_EXPIRE = 30000;

    @Getter
    private final String requestID;

    @Getter
    private UUID requestingPlayer, requestedPlayer;

    private long requestTime;

    public TradeRequest(UUID requestingPlayer, UUID requestedPlayer) {
        this.requestID = "";
        this.requestingPlayer = requestingPlayer;
        this.requestedPlayer = requestedPlayer;

        this.requestTime = System.currentTimeMillis();
    }

    /**
     * Check if the trade request has expired
     *
     * @return
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() - this.requestTime >= REQUEST_TIME_EXPIRE;
    }

    public Trade createTrade() {

        if (!hasExpired()) {
            return new Trade(this.requestingPlayer, this.requestedPlayer);
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof TradeRequest) {
            return this.requestID.equals(((TradeRequest) o).getRequestID());
        }

        return false;
    }
}
