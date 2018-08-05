package com.nuno1212s.tradewindow.trades;

import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.tradewindow.TradeItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class Trade {

    @Getter
    private final UUID player1, player2;

    @Getter
    @Setter
    private boolean player1Accepted, player2Accepted;

    @Getter
    private long player1Coins, player2Coins;

    @Getter
    private Inventory player1Inv, player2Inv;

    public Trade(UUID player1, UUID player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Coins = 0;
        this.player2Coins = 0;

        this.player1Inv = TradeMain.getIns().getTradeInventory().buildInventory(this);
        this.player2Inv = TradeMain.getIns().getTradeInventory().buildInventory(this);

        this.player1Accepted = false;
        this.player2Accepted = false;
    }


    /**
     * Is the player provided the player 1
     *
     * @param player The ID of the player to check
     * @return
     */
    public boolean isPlayer1(UUID player) {
        return player1.equals(player);
    }

    /**
     * Is the player provided the player 2
     *
     * @param player The ID of the player to check
     * @return
     */
    public boolean isPlayer2(UUID player) {
        return player2.equals(player);
    }

    public Inventory getPlayerInv(UUID player) {

        if (isPlayer1(player)) {
            return this.player1Inv;
        }

        return player2Inv;
    }

    public void playerChangedTrade() {
        this.player2Accepted = false;
        this.player1Accepted = false;

        updateAcceptItems();

        MainData.getIns().getScheduler().runTask(this::updateInventories);
    }

    public void updateInventories() {

        if (player1Inv != null && player2Inv != null) {
            copyItems(player1Inv, player2Inv);

            copyItems(player2Inv, player1Inv);
        }

    }

    private void copyItems(Inventory inv1, Inventory inv2) {
        List<? extends InventoryItem> player1 = TradeMain.getIns().getTradeInventory().getItemsWithFlag("SELF");

        for (InventoryItem inventoryItem : player1) {

            ItemStack item = inv1.getItem(inventoryItem.getSlot());

            ItemStack item1 = inv2.getItem(inventoryItem.getSlot() + 5);

            if (item1 != null && item1.equals(item)) continue;

            inv2.setItem(inventoryItem.getSlot() + 5, item);
        }
    }

    public void updateAcceptItems() {

        List<? extends InventoryItem> acceptItem = TradeMain.getIns().getTradeInventory().getItemsWithFlag("ACCEPT");

        for (InventoryItem inventoryItem : acceptItem) {
            if (inventoryItem.hasItemFlag("SELF_ACCEPT")) {

                player1Inv.setItem(inventoryItem.getSlot(), getPlayer1AcceptItem());

                player2Inv.setItem(inventoryItem.getSlot(), getPlayer2AcceptItem());

            } else {

                player1Inv.setItem(inventoryItem.getSlot(), getPlayer2AcceptItem());

                player2Inv.setItem(inventoryItem.getSlot(), getPlayer1AcceptItem());

            }
        }
    }

    public void updateCoinsItems() {

        List<TradeItem> coinsItem = TradeMain.getIns().getTradeInventory().getItemsWithFlag("COINS");

        for (TradeItem inventoryItem : coinsItem) {

            if (inventoryItem.hasItemFlag("SELF_COINS")) {

                player1Inv.setItem(inventoryItem.getSlot(), inventoryItem.getItem(this.player1Coins));

                player2Inv.setItem(inventoryItem.getSlot(), inventoryItem.getItem(this.player2Coins));

            } else {

                player1Inv.setItem(inventoryItem.getSlot(), inventoryItem.getItem(this.player2Coins));

                player2Inv.setItem(inventoryItem.getSlot(), inventoryItem.getItem(this.player1Coins));

            }

        }

    }

    public ItemStack getPlayer1AcceptItem() {
        return this.player1Accepted ? TradeMain.getIns().getTradeManager().getAcceptedItem() : TradeMain.getIns().getTradeManager().getRejectedItem();
    }

    public ItemStack getPlayer2AcceptItem() {
        return this.player2Accepted ? TradeMain.getIns().getTradeManager().getAcceptedItem() : TradeMain.getIns().getTradeManager().getRejectedItem();
    }

    public void playerToggleAccept(UUID player) {

        if (isPlayer1(player)) {
            this.player1Accepted = !this.player1Accepted;
        } else {
            this.player2Accepted = !this.player2Accepted;
        }

        updateAcceptItems();

        checkTrade();

    }

    /**
     * Checks if the trade can be executed and if it can, executes it
     */
    private void checkTrade() {

        if (this.player1Accepted && this.player2Accepted) {
            doTrade();
        }

    }

    private void doTrade() {

        PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(this.player1),
                player2 = MainData.getIns().getPlayerManager().getPlayer(this.player2);

        if (player1.isPlayerOnServer() && player2.isPlayerOnServer()) {

            TradeMain.getIns().getTradeManager().finishTrade(this);

            player1.getPlayerReference(Player.class).closeInventory();

            player2.getPlayerReference(Player.class).closeInventory();

            giveItems("SELF", player1Inv, player2.getPlayerReference(Player.class));

            giveItems("SELF", player2Inv, player1.getPlayerReference(Player.class));

            MainData.getIns().getServerCurrencyHandler().addCurrency(player1, this.player2Coins);

            MainData.getIns().getServerCurrencyHandler().addCurrency(player2, this.player1Coins);

        } else {

            //Players participating in trade not in the server, destroy the trade

            TradeMain.getIns().getTradeManager().destroyTrade(this);
        }

    }

    /**
     * Set the coins on the trade
     *
     * @param player
     * @param coins
     */
    public void setCoins(UUID player, long coins) {
        if (isPlayer1(player)) {
            this.player1Coins = coins;
        } else {
            this.player2Coins = coins;
        }

        updateCoinsItems();
    }

    /**
     * Destroys this trade and returns everything to their owners
     */
    void destroyTrade() {

        Player player1 = Bukkit.getServer().getPlayer(this.getPlayer1()), player2 = Bukkit.getServer().getPlayer(this.getPlayer2());

        try {

            if (player1.getOpenInventory() != null && player1.getOpenInventory().getTitle().equalsIgnoreCase(this.player1Inv.getName())) {
                player1.closeInventory();
            }

            if (player2.getOpenInventory() != null && player2.getOpenInventory().getTitle().equalsIgnoreCase(this.player2Inv.getName())) {
                player2.closeInventory();
            }

        } finally {

            //Even if there is an error with closing the inventories, this code will be executed

            /*
            Give items to the original owner
             */
            giveItems("SELF", player1Inv, player1);

            giveItems("SELF", player2Inv, player2);

            PlayerData pD1 = MainData.getIns().getPlayerManager().getPlayer(this.player1),
                    pD2 = MainData.getIns().getPlayerManager().getPlayer(this.player2);

            MainData.getIns().getServerCurrencyHandler().addCurrency(pD1, this.player1Coins);

            MainData.getIns().getServerCurrencyHandler().addCurrency(pD2, this.player2Coins);
        }

    }

    /**
     * Give the items in the slots with the given flag to the given playerInstance
     *
     * @param flag           The flag to look for
     * @param inv            The inventory to get the items from
     * @param playerInstance The player instance
     */
    private void giveItems(String flag, Inventory inv, Player playerInstance) {

        List<? extends InventoryItem> playerSlots = TradeMain.getIns().getTradeInventory().getItemsWithFlag(flag);

        for (InventoryItem playerSlot : playerSlots) {
            ItemStack item = inv.getItem(playerSlot.getSlot());

            if (item != null && item.getType() != Material.AIR) {

                playerInstance.getInventory().addItem(item);

            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trade) {
            return this.player1.equals(((Trade) obj).getPlayer1()) && this.player2.equals(((Trade) obj).getPlayer2());
        }

        return false;
    }
}
