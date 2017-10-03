package com.nuno1212s.hub.npcs;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private UUID npcID;

    private String connectingServer;

    private double x, y, z;

    private String world;

    private transient List<WeakReference<UUID>> waitingList;

    private transient Pair<Integer, Integer> latestPlayerCount;

    public NPC() {
        this.waitingList = new ArrayList<>();
        this.latestPlayerCount = new Pair<>(-1, -1);
    }

    public NPC(UUID npcID, String connectingServer, double x, double y, double z, String world) {
        this.npcID = npcID;
        this.connectingServer = connectingServer;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.waitingList = new ArrayList<>();
        this.latestPlayerCount = new Pair<>(-1, -1);
    }

    /**
     * Handle a player clicking the NPC
     * @param p
     */
    public void handleClick(Player p) {
        if (latestPlayerCount.key() >= latestPlayerCount.value()) {
            this.waitingList.add(new WeakReference<>(p.getUniqueId()));
            return;
        }

        if (!waitingList.isEmpty()) {
            waitingList.add(new WeakReference<>(p.getUniqueId()));
            handleWaitingList();
            return;
        }

    }

    /**
     * Handle the waiting list
     */
    public void handleWaitingList() {
        int currentPlayerSpace = latestPlayerCount.value() - latestPlayerCount.key();

        if (currentPlayerSpace >= this.waitingList.size()) {
            currentPlayerSpace = this.waitingList.size();
        }

        for (int i = 0; i < currentPlayerSpace; i++) {
            UUID uuid = this.waitingList.get(i).get();
        }
    }

    /**
     * Update the NPC names
     */
    public void updateNPC() {
        List<LivingEntity> livingEntities = Bukkit.getWorld(world).getLivingEntities();
        LivingEntity entities = null;

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity.getUniqueId().equals(npcID)) {
                entities = livingEntity;
                break;
            }
        }

        if (entities == null) {
            return;
        }

        this.latestPlayerCount = MainData.getIns().getServerManager().getPlayerCount(connectingServer);

        Message server_player_count = MainData.getIns().getMessageManager().getMessage("SERVER_PLAYER_COUNT");
        entities.setCustomName(server_player_count.format("%playerAmount%", String.valueOf(latestPlayerCount.key()))
                .format("%maxplayers%", String.valueOf(latestPlayerCount.value())).toString());
        entities.setCustomNameVisible(true);

    }

}
