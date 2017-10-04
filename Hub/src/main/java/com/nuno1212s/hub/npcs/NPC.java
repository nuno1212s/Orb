package com.nuno1212s.hub.npcs;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class NPC {

    @Getter
    private UUID npcID;

    private String connectingServer;

    private double x, y, z;

    private String world;

    public NPC(LivingEntity entity, String connectingServer) {
        this.npcID = entity.getUniqueId();
        this.connectingServer = connectingServer;
        this.x = entity.getLocation().getX();
        this.y = entity.getLocation().getY();
        this.z = entity.getLocation().getZ();
        this.world = entity.getWorld().getName();
    }

    public NPC(UUID npcID, String connectingServer, double x, double y, double z, String world) {
        this.npcID = npcID;
        this.connectingServer = connectingServer;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    /**
     * Handle a player clicking the NPC
     *
     * @param p
     */
    public void handleClick(Player p) {
        Main.getIns().getServerSelectorManager().sendPlayerToServer(p, this.connectingServer);
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

        Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(this.connectingServer);

        Message server_player_count = MainData.getIns().getMessageManager().getMessage("SERVER_PLAYER_COUNT");
        entities.setCustomName(server_player_count.format("%playerAmount%", String.valueOf(playerCount.key()))
                .format("%maxplayers%", String.valueOf(playerCount.value())).toString());
        entities.setCustomNameVisible(true);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (obj instanceof NPC) {
            return ((NPC) obj).getNpcID().equals(this.getNpcID());
        } else if (obj instanceof Entity) {
            return ((Entity) obj).getUniqueId().equals(this.getNpcID());
        }

        return false;
    }
}
