package com.nuno1212s.tradewindow.tradewindow;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.trades.Trade;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeInventory extends InventoryData<TradeItem> {

    public TradeInventory(File file) {
        super(file, TradeItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        TradeItem item = getItem(e.getSlot());

        Trade t = TradeMain.getIns().getTradeManager().getTrade(e.getWhoClicked().getUniqueId());

        if (item != null) {

            if (item.hasItemFlag("PLAYER1_ACCEPT")) {
                e.setResult(Event.Result.DENY);

                if (!t.player1ToggleAccept(e.getWhoClicked().getUniqueId())) {
                    return;
                }

                if (t.isPlayer1Accepted()) {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getAcceptedItem());
                } else {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
                }

            } else if (item.hasItemFlag("PLAYER2_ACCEPT")) {
                e.setResult(Event.Result.DENY);

                if (!t.player2ToggleAccept(e.getWhoClicked().getUniqueId())) {
                    return;
                }

                if (t.isPlayer2Accepted()) {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getAcceptedItem());
                } else {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
                }

            } else if (item.hasItemFlag("COINS")) {
                e.setResult(Event.Result.DENY);

                if (item.hasItemFlag("PLAYER1_COINS") && !t.isPlayer1(e.getWhoClicked().getUniqueId())) {
                    return;
                } else if (item.hasItemFlag("PLAYER2_COINS") && !t.isPlayer2(e.getWhoClicked().getUniqueId())) {
                    return;
                }

                TradeMain.getIns().getTradeManager().getCloseExceptions().add(e.getWhoClicked().getUniqueId());

                e.getWhoClicked().closeInventory();

                requestCoinsFromChat((Player) e.getWhoClicked(), t);

            } else if (canPlayerPlace(t, (Player) e.getWhoClicked(), e.getSlot())) {

                t.playerChangedTrade();

                List<TradeItem> accept = getItemsWithFlag("ACCEPT");

                for (TradeItem tradeItem : accept) {
                    e.getClickedInventory().setItem(tradeItem.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
                }

            } else {
                e.setResult(Event.Result.DENY);
            }
        }
    }

    /**
     * Request the coin amount from the chat
     *
     * @param player
     * @param t
     */
    public void requestCoinsFromChat(Player player, Trade t) {
        TradeMain.getIns().getChatRequests().requestChatInformation(player, "INSERT_COIN_AMOUND")
                .thenAccept((message) -> {
                    int coins;

                    try {

                        coins = Integer.parseInt(message);

                    } catch (NumberFormatException ex) {

                        MainData.getIns().getMessageManager().getMessage("COINS_MUST_BE_NUMBER")
                                .sendTo(player);

                        requestCoinsFromChat(player, t);

                        return;
                    }

                    PlayerData pD = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

                    MainData.getIns().getServerCurrencyHandler().removeCurrency(pD, coins)
                            .thenAccept((result) -> {

                                if (result) {

                                    t.setCoins(player.getUniqueId(), coins);

                                    Inventory tradeInventory = t.getTradeInventory();

                                    player.openInventory(tradeInventory);

                                    updateCoinItems(t);
                                } else {

                                    MainData.getIns().getMessageManager().getMessage("NOT_ENOUGH_COINS")
                                            .format("%coins%", coins)
                                            .sendTo(pD);

                                    requestCoinsFromChat(player, t);
                                }
                            });
                });
    }

    /**
     * Check if a player can place an item in a position
     *
     * @param t      The trade in question
     * @param player The player that tried to player the item
     * @param slot   The slot the item was placed in
     * @return
     */
    public boolean canPlayerPlace(Trade t, Player player, int slot) {

        TradeItem item = getItem(slot);

        if (item == null) {
            return false;
        }

        if (t.isPlayer1(player.getUniqueId())) {
            return item.hasItemFlag("PLAYER1");
        }

        return item.hasItemFlag("PLAYER2");
    }

    /**
     * Update the items that represent coins
     * @param t
     */
    public void updateCoinItems(Trade t) {
        for (TradeItem coins : this.getItemsWithFlag("COINS")) {
            t.getTradeInventory().setItem(coins.getSlot(), coins.getItem(t));
        }
    }

    public Inventory buildInventory(Trade t) {
        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (TradeItem item : this.items) {
            i.setItem(item.getSlot(), item.getItem(t));
        }

        return i;
    }

}

class TradeItem extends InventoryItem {

    public TradeItem(JSONObject data) {
        super(data);
    }

    public ItemStack getItem(Trade t) {

        Map<String, String> formats = new HashMap<>();

        formats.put("%player1Coins%", String.valueOf(t.getPlayer1Coins()));
        formats.put("%player2Coins%", String.valueOf(t.getPlayer2Coins()));

        return ItemUtils.formatItem(this.item.clone(), formats);
    }

}
