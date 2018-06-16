package com.nuno1212s.hub.server_selector;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.util.HInventoryData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.server_sender.BukkitSender;
import com.nuno1212s.util.Pair;
import com.nuno1212s.inventories.InventoryData;
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

    @Getter
    private Map<UUID, String> openInventories;

    private Map<String, List<UUID>> waitingList;

    public ServerSelectorManager(Module m) {
        openInventories = new HashMap<>();
        waitingList = new HashMap<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File[] files = dataFolder.listFiles();

        for (File file : files) {
            new HInventoryData(file);
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

        MainData.getIns().getScheduler().runTaskTimerAsync(this::handleWaitingList, 0, 5);
    }

    /**
     * Get the main inventory
     *
     * @return
     */
    public Inventory getMainInventory() {
        return MainData.getIns().getInventoryManager().getInventory("HubLandingInventory").buildInventory();
    }

    /**
     * Update all the open inventories
     */
    public void updateInventories() {

        this.openInventories.forEach((player, inventory) -> {
            Player p = Bukkit.getPlayer(player);

            if (p == null || !p.isOnline()) {
                this.openInventories.remove(player);
                return;
            }

            Inventory inventoryByName = MainData.getIns().getInventoryManager().getInventory(inventory)
                    .buildInventory();

            if (inventoryByName != null) {
                p.getOpenInventory().getTopInventory().setContents(inventoryByName.getContents());
            }
        });

    }

    /**
     * Send a player to a server
     *
     * @param serverName
     */
    public void sendPlayerToServer(Player p, String serverName) {
        Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(serverName);
        if (playerCount.key() >= playerCount.value()) {
            List<UUID> currentPlayerWaitingList = this.waitingList.getOrDefault(serverName.toLowerCase(), new ArrayList<>());

            if (currentPlayerWaitingList.contains(p.getUniqueId())) {
                return;
            }

            currentPlayerWaitingList.add(p.getUniqueId());

            this.waitingList.put(serverName.toLowerCase(), currentPlayerWaitingList);

            MainData.getIns().getMessageManager().getMessage("ADDED_TO_WAITING_LIST")
                    .format("%listPlace%", String.valueOf(currentPlayerWaitingList.size())).sendTo(p);
        } else {
            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            BukkitSender.getIns().sendPlayer(player, p, serverName);
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

                    BukkitSender.getIns().sendPlayer(MainData.getIns().getPlayerManager().getPlayer(playerID), p, server);
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

                    BukkitSender.getIns().sendPlayer(MainData.getIns().getPlayerManager().getPlayer(player), p, server);
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
