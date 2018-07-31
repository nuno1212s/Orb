package com.nuno1212s.tradewindow;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.tradewindow.trades.TradeManager;
import lombok.Getter;

@ModuleData(name = "Trade Window", version = "1.0 ALPHA")
public class TradeMain extends Module {

    @Getter
    static TradeMain ins;

    @Getter
    TradeManager tradeManager;

    @Override
    public void onEnable() {
        tradeManager = new TradeManager(this);
    }

    @Override
    public void onDisable() {

    }
}
