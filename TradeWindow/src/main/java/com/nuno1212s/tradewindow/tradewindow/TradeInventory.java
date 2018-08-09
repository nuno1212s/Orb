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
import java.util.Map;

public class TradeInventory extends InventoryData<TradeItem> {

    public TradeInventory(File file) {
        super(file, TradeItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        TradeItem item = getItem(e.getSlot());

        Trade t = TradeMain.getIns().getTradeManager().getTrade(e.getWhoClicked().getUniqueId());

        if (t == null) {
            e.setResult(Event.Result.DENY);
            return;
        }

        if (item != null) {

            if (item.hasItemFlag("ACCEPT")) {
                e.setResult(Event.Result.DENY);

                if (item.hasItemFlag("SELF_ACCEPT")) {

                    t.playerToggleAccept(e.getWhoClicked().getUniqueId());

                }

            } else if (item.hasItemFlag("COINS")) {
                e.setResult(Event.Result.DENY);

                if (item.hasItemFlag("SELF")) {

                    e.getWhoClicked().closeInventory();

                    requestCoinsFromChat((Player) e.getWhoClicked(), t);

                }

            } else if (canPlayerPlace(e.getSlot())) {

                t.playerChangedTrade();

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

                                    Inventory tradeInventory = t.getPlayerInv(pD.getPlayerID());

                                    player.openInventory(tradeInventory);

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
     * @param slot The slot the item was placed in
     * @return
     */
    public boolean canPlayerPlace(int slot) {

        TradeItem item = getItem(slot);

        if (item == null) {
            return false;
        }

        return item.hasItemFlag("SELF");
    }

    public Inventory buildInventory(Trade t) {
        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (TradeItem item : this.items) {
            if (item.hasItemFlag("ACCEPT")) {
                i.setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
            } else {
                i.setItem(item.getSlot(), item.getItem(0));
            }
        }

        return i;
    }

}

