package com.nuno1212s.mercado.redishandler;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class MRedisListener implements RedisReceiver {

    /**
     * Register an item being sold
     *
     * @param item
     */
    public void sellItem(Item item) {
        JSONObject data = new JSONObject();
        data.put("ItemID", item.getItemID());
        data.put("APPLICABLESERVER", item.getApplicableServer());
        data.put("BUYER", item.getBuyer().toString());
        data.put("SOLDTIME", item.getSoldTime());
        data.put("SOLD", item.isSold());

        Message message = new Message(channel(), "SOLD", data);
        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    /**
     * Remove an item from the server data
     * @param item
     */
    public void removeItem(Item item) {
        JSONObject data = new JSONObject();
        data.put("ItemID", item.getItemID());
        data.put("APPLICABLESERVER", item.getApplicableServer());

        Message message = new Message(channel(), "REMOVE", data);
        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    /**
     * Add an item to the server data
     * @param item
     */
    public void addItem(Item item) {
        JSONObject data = new JSONObject();
        data.put("ItemID", item.getItemID());
        data.put("APPLICABLESERVER", item.getApplicableServer());
        data.put("ITEM", ItemUtils.itemTo64(item.getItem()));
        data.put("BUYER", item.getBuyer() == null ? "" : item.getBuyer().toString());
        data.put("OWNER", item.getOwner().toString());
        data.put("COST", item.getCost());
        data.put("PLACETIME", item.getPlaceTime());
        data.put("SOLDTIME", item.getSoldTime());
        data.put("SERVERCURRENCY", item.isServerCurrency());
        data.put("SOLD", item.isSold());

        Message message = new Message(channel(), "ADD", data);
        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    @Override
    public String channel() {
        return "MARKET";
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("SOLD")) {
                JSONObject data = message.getData();
                String itemID = (String) data.get("ItemID");
                String applicableServer = (String) data.get("APPLICABLESERVER");
                UUID buyer = UUID.fromString((String) data.get("BUYER"));
                long soldTime = (Long) data.get("SOLDTIME");
                boolean sold = (Boolean) data.get("SOLD");

                if (MainData.getIns().getServerManager().getServerType().equalsIgnoreCase(applicableServer)) {
                    Item item = Main.getIns().getMarketManager().getItem(itemID);
                    if (item == null) {
                        return;
                    }
                    item.setSoldTime(soldTime);
                    item.setSold(sold);
                    item.setBuyer(buyer);
                }
            } else if (message.getReason().equalsIgnoreCase("REMOVE")) {
                JSONObject data = message.getData();
                String itemID = (String) data.get("ItemID");
                String applicableServer = (String) data.get("ApplicableServer");
                if (MainData.getIns().getServerManager().getServerType().equalsIgnoreCase(applicableServer)) {
                    Item item = Main.getIns().getMarketManager().getItem(itemID);
                    if (item == null) {
                        return;
                    }
                    Main.getIns().getMarketManager().removeItem(itemID);
                }
            } else if (message.getReason().equalsIgnoreCase("ADD")) {
                JSONObject data = message.getData();
                String itemID = (String) data.get("ItemID");
                String applicableServer = (String) data.get("APPLICABLESERVER");

                if (!MainData.getIns().getServerManager().getServerType().equalsIgnoreCase(applicableServer)) {
                    return;
                }

                UUID owner = UUID.fromString((String) data.get("OWNER"));
                UUID buyer = UUID.fromString((String) data.get("BUYER"));

                ItemStack item;
                try {
                    item = ItemUtils.itemFrom64((String) data.get("ITEM"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                long cost = (Long) data.get("COST"),
                        placeTime = (Long) data.get("PLACETIME"),
                        soldTime = (Long) data.get("SOLDTIME");
                boolean serverCurrency = (Boolean) data.get("SERVERCURRENCY"),
                        sold = (Boolean) data.get("SOLD");
                Item i = new Item(itemID, owner, buyer, item, cost, placeTime,
                        soldTime, serverCurrency, sold, applicableServer);
                Main.getIns().getMarketManager().addItem(i);
            }
        }
    }
}
