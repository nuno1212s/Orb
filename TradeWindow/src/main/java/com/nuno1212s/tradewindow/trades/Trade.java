package com.nuno1212s.tradewindow.trades;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class Trade {

    @Getter
    private final UUID player1, player2;

    @Getter
    @Setter
    private boolean player1Accepted, player2Accepted;

    @Getter
    private Inventory tradeInventory;

    public Trade(UUID player1, UUID player2, Inventory tradeInventory) {
        this.player1 = player1;
        this.player2 = player2;
        this.tradeInventory = tradeInventory;
        this.player1Accepted = false;
        this.player2Accepted = false;
    }

}
