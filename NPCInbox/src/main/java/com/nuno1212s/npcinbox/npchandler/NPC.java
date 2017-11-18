package com.nuno1212s.npcinbox.npchandler;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.WeakHashMap;

public class NPC {

    @Getter
    private UUID entityID;

    private transient WeakReference<Entity> entityReference;

    private transient WeakHashMap<PlayerData, Hologram> holograms;

    public NPC(UUID entityID) {
        this.entityID = entityID;

        this.entityReference = new WeakReference<Entity>(CitizensAPI.getNPCRegistry().getByUniqueId(entityID).getEntity());
        this.holograms = new WeakHashMap<>();
    }

    /**
     * Get the npc entity
     *
     * @return
     */
    public Entity getEntity() {

        if (entityReference == null || entityReference.get() == null) {
            return null;
        }

        return entityReference.get();
    }

    /**
     * Remove the hologram for a player
     *
     * @param data
     */
    public void removeHologram(PlayerData data) {
        if (this.holograms.containsKey(data)) {
            this.holograms.get(data).delete();
            this.holograms.remove(data);
        }
    }

    /**
     * Display the amount of NPC Inbox
     *
     * @param data The player to display them to
     */
    public void displayInformation(PlayerData data) {
        Entity entity = getEntity();

        if (entity == null) {
            return;
        }

        Location hologram = entity.getLocation().clone().add(0, 2, 0);

        Hologram playerHologram = this.holograms.get(data);

        TextLine textLine;

        if (playerHologram == null) {
            playerHologram = HologramsAPI.createHologram(BukkitMain.getIns(), hologram);

            playerHologram.getVisibilityManager().setVisibleByDefault(false);
            playerHologram.getVisibilityManager().showTo(data.getPlayerReference(Player.class));

            textLine = playerHologram.appendTextLine("");
        } else {
            textLine = (TextLine) playerHologram.getLine(0);
        }

        if (!data.getToClaim().isEmpty()) {
            String s = MainData.getIns().getMessageManager().getMessage("NPC_TO_CLAIM")
                    .format("%rewardAmount%", String.valueOf(data.getToClaim().size())).toString();

            textLine.setText(s);
        } else {
            String s = MainData.getIns().getMessageManager().getMessage("NPC_NO_CLAIM")
                    .toString();

            textLine.setText(s);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj instanceof NPC) {
            return ((NPC) obj).getEntityID().equals(getEntityID());
        } else if (obj instanceof UUID) {
            return obj.equals(getEntityID());
        } else {
            return false;
        }
    }
}
