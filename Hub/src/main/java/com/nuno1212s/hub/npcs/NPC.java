package com.nuno1212s.hub.npcs;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class NPC {

    @Getter
    private UUID npcID;

    private String connectingServer;

    private double x, y, z;

    private String world;

    private String displayName;

    private transient Hologram hologram;

    public NPC(LivingEntity entity, String connectingServer, String displayName) {
        this.npcID = entity.getUniqueId();

        net.citizensnpcs.api.npc.NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        npc.setName("");

        this.connectingServer = connectingServer;
        this.displayName = displayName;
        this.x = entity.getLocation().getX();
        this.y = entity.getLocation().getY();
        this.z = entity.getLocation().getZ();
        this.world = entity.getWorld().getName();

        createHologram();
    }

    public NPC(UUID npcID, String connectingServer, String displayName, double x, double y, double z, String world) {
        this.npcID = npcID;
        this.displayName = displayName;
        this.connectingServer = connectingServer;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Hologram getCorrespondingHologram() {
        int blockX = NumberConversions.floor(x), blockY = NumberConversions.floor(y), blockZ = NumberConversions.floor(z);
        Collection<Hologram> holograms = HologramsAPI.getHolograms(BukkitMain.getIns());

        for (Hologram hologram1 : holograms) {
            Location location = hologram1.getLocation();
            if (location.getWorld().getName().equalsIgnoreCase(this.world) && location.getBlockX() == blockX && location.getBlockY() == blockY && location.getBlockZ() == blockZ) {
                return hologram1;
            }
        }

        return createHologram();
    }

    private Hologram createHologram() {
        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        Hologram hologram = HologramsAPI.createHologram(BukkitMain.getIns(), new Location(world, x, y + 2.5, z));

        hologram.appendTextLine(displayName);
        Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(this.connectingServer);

        Message server_player_count = MainData.getIns().getMessageManager().getMessage("SERVER_PLAYER_COUNT");
        String name = server_player_count.format("%playerAmount%", String.valueOf(playerCount.key()))
                .format("%maxplayers%", String.valueOf(playerCount.value())).toString();

        hologram.appendTextLine(name);

        return hologram;
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

        Pair<Integer, Integer> playerCount = MainData.getIns().getServerManager().getPlayerCount(this.connectingServer);

        Message server_player_count = MainData.getIns().getMessageManager().getMessage("SERVER_PLAYER_COUNT");
        String name = server_player_count.format("%playerAmount%", String.valueOf(playerCount.key()))
                .format("%maxplayers%", String.valueOf(playerCount.value())).toString();

        if (this.hologram == null) {
            this.hologram = getCorrespondingHologram();
        }

        TextLine line = (TextLine) this.hologram.getLine(1);

        line.setText(name);

    }

    public void deleteNPC() {
        hologram.getVisibilityManager().setVisibleByDefault(false);
        hologram.getVisibilityManager().resetVisibilityAll();
        for (int i = 0; i < hologram.size(); i++) {
            this.hologram.getLine(i).removeLine();
        }
        this.hologram.delete();
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
