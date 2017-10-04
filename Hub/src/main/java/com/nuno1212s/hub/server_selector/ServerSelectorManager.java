package com.nuno1212s.hub.server_selector;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.util.HInventoryData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.BungeeSender;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.inventories.InventoryData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.*;

/**
 * Inventory selecting manager
 */
public class ServerSelectorManager {

    private List<HInventoryData> inventories;

    @Getter
    private List<UUID> openInventories;

    private Map<String, List<UUID>> waitingList;

    public ServerSelectorManager(Module m) {
        inventories = new ArrayList<>();
        openInventories = new ArrayList<>();
        waitingList = new HashMap<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File[] files = dataFolder.listFiles();

        for (File file : files) {
            inventories.add(new HInventoryData(file.getName().replace(".json", ""), file));
        }

        MainData.getIns().getScheduler().runTaskTimer(() -> {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for (Player onlinePlayer : onlinePlayers) {
                PlayerData d = MainData.getIns().getPlayerManager().getPlayer(onlinePlayer.getUniqueId());
                DisplayMain.getIns().getScoreboardManager().createScoreboard(d, onlinePlayer);
            }

            Main.getIns().getNpcManager().updateNPCs();
            updateInventories();

        }, 20, 20);

        MainData.getIns().getScheduler().runTaskTimerAsync(() -> {
            MainData.getIns().getServerManager().fetchServerData((ob) -> {

                handleWaitingList();
            });
        }, 0, 5);
    }

    /**
     * Get the inventory data for an inventory
     *
     * @param inventoryID
     * @return
     */
    public InventoryData getInventoryData(String inventoryID) {
        for (HInventoryData inventory : inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory;
            }
        }

        return null;
    }

    /**
     * Get the inventory data with the inventory name
     *
     * @param inventoryName
     * @return
     */
    public InventoryData getInventoryDataByName(String inventoryName) {
        for (HInventoryData inventory : inventories) {
            if (inventory.getInventoryName().equalsIgnoreCase(inventoryName)) {
                return inventory;
            }
        }
        return null;
    }

    /**
     * Get the inventory data
     *
     * @param inventoryID
     * @return
     */
    public Inventory getInventory(String inventoryID) {
        InventoryData inventoryData = getInventoryData(inventoryID);

        if (inventoryData == null) {
            return null;
        }

        return inventoryData.buildInventory();
    }

    /**
     * Get the main inventory
     *
     * @return
     */
    public Inventory getMainInventory() {
        return getInventory("landingInventory");
    }

    /**
     * @param inventoryName
     * @return
     */
    public Inventory getInventoryByName(String inventoryName) {
        InventoryData inventoryDataByName = getInventoryDataByName(inventoryName);

        if (inventoryDataByName == null) {
            return null;
        }

        return inventoryDataByName.buildInventory();
    }

    /**
     * Update all the open inventories
     */
    public void updateInventories() {
        Iterator<UUID> iterator = openInventories.iterator();

        while (iterator.hasNext()) {
            UUID openInventory = iterator.next();
            Player p = Bukkit.getPlayer(openInventory);

            if (p == null || !p.isOnline()) {
                iterator.remove();
                return;
            }

            Inventory inventoryByName = getInventoryByName(p.getOpenInventory().getTopInventory().getName());

            if (inventoryByName != null) {
                p.getOpenInventory().getTopInventory().setContents(inventoryByName.getContents());
            }
        }
    }

    /**
     * Send a player to a server
     *
     * @param serverName
     */
    public void sendPlayerToServer(Player p, String serverName) {
        Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(serverName);
        if (playerCount.key() >= playerCount.value()) {
            List<UUID> currentPlayerWaitingList = this.waitingList.getOrDefault(serverName, new ArrayList<>());

            if (currentPlayerWaitingList.contains(p.getUniqueId())) {
                return;
            }

            currentPlayerWaitingList.add(p.getUniqueId());

            this.waitingList.put(serverName, currentPlayerWaitingList);

            MainData.getIns().getMessageManager().getMessage("ADDED_TO_WAITING_LIST")
                    .format("%listPlace%", String.valueOf(currentPlayerWaitingList.size())).sendTo(p);
        } else {
            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());
            BungeeSender.getIns().sendPlayer(player, p, serverName);
        }
    }

    /**
     * Handles the waiting list
     */
    public void handleWaitingList() {
        this.waitingList.forEach((server, players) -> {
            Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(server);

            int playerWaitingAmount = players.size(), playerSpace = playerCount.value() - playerCount.key();

            if (playerWaitingAmount >= playerSpace) {
                Iterator<UUID> iterator = players.iterator();

                int i = 0;

                while (i < playerSpace && iterator.hasNext()) {
                    UUID playerID = iterator.next();

                    iterator.remove();

                    Player p = Bukkit.getPlayer(playerID);

                    if (p == null) {
                        continue;
                    }

                    i++;

                    MainData.getIns().getMessageManager().getMessage("ATTEMPTING_TO_SEND").sendTo(p);

                    BungeeSender.getIns().sendPlayer(MainData.getIns().getPlayerManager().getPlayer(playerID), p, server);
                }
            } else {
                Iterator<UUID> iterator = players.iterator();

                while (iterator.hasNext()) {
                    UUID player = iterator.next();
                    iterator.remove();

                    Player p = Bukkit.getPlayer(player);

                    if (p == null) {
                        continue;
                    }

                    BungeeSender.getIns().sendPlayer(MainData.getIns().getPlayerManager().getPlayer(player), p, server);
                }
            }

        });
    }

    /**
     * Handle a player disconnecting
     *
     * @param p
     */
    public void handlePlayerDisconnect(Player p) {
        this.waitingList.forEach((server, players) -> {
            if (players.contains(p.getUniqueId())) {
                players.remove(p.getUniqueId());
            }
        });
    }

}
