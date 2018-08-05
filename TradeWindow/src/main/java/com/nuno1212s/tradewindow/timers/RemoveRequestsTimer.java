package com.nuno1212s.tradewindow.timers;

import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.trades.TradeRequest;

public class RemoveRequestsTimer implements Runnable {

    @Override
    public void run() {

        synchronized (TradeMain.getIns().getTradeManager().getTradingRequests()) {
            TradeMain.getIns().getTradeManager().getTradingRequests().removeIf(TradeRequest::hasExpired);
        }

    }
}
