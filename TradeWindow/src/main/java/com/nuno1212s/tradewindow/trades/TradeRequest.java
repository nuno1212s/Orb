package com.nuno1212s.tradewindow.trades;

import com.nuno1212s.tradewindow.TradeMain;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TradeRequest {

    private static int REQUEST_TIME_EXPIRE = 30000;

    @Getter
    private final String requestID;

    @Getter
    private UUID requestingPlayer, requestedPlayer;

    private long requestTime;

    public TradeRequest(UUID requestingPlayer, UUID requestedPlayer) {
        this.requestID = RandomStringUtils.random(5, true, true);

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
            Trade trade = new Trade(this.requestingPlayer, this.requestedPlayer);

            TradeMain.getIns().getTradeManager().addTrade(trade);

            TradeMain.getIns().getTradeManager().removeTradeRequest(this);

            Player player1 = Bukkit.getPlayer(this.requestingPlayer), player2 = Bukkit.getPlayer(this.requestedPlayer);

            if (player1 != null && player2 != null && player1.isOnline() && player2.isOnline()) {

                player1.openInventory(trade.getPlayer1Inv());

                player2.openInventory(trade.getPlayer2Inv());

            } else {
                throw new IllegalArgumentException("Players have to be online");
            }

            return trade;
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
