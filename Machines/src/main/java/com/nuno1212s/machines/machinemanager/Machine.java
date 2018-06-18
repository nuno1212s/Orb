package com.nuno1212s.machines.machinemanager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.machines.players.MachinePlayer;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Machine {

    @Getter
    private UUID owner, machineID;

    private int configuration;

    @Getter
    private int amount;

    @Getter
    private LLocation machineLocation;


    private transient long currentSpacing;

    private transient Hologram hologram;

    public Machine(UUID owner, MachineConfiguration machineConfiguration, LLocation location) {
        this.machineID = UUID.randomUUID();
        this.owner = owner;
        this.configuration = machineConfiguration.getId();
        this.machineLocation = location;
    }

    public MachineConfiguration getConfiguration() {
        return Main.getIns().getMachineManager().getConfiguration(this.configuration);
    }

    /**
     * Updates the holograms above the machine
     */
    public void updateName() {

        Message machine_name = MainData.getIns().getMessageManager().getMessage("MACHINE_NAME");
        MachineConfiguration configuration = this.getConfiguration();

        machine_name.format("%amount%", this.amount);
        machine_name.format("%baseAmount%", configuration.getBaseAmount());
        machine_name.format("%currentAmount%", configuration.getBaseAmount() * this.amount);
        machine_name.format("%timeDifferent%", DurationFormatUtils.formatDuration(configuration.getSpacing(), "mm:ss"));

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(BukkitMain.getIns(), this.getMachineLocation().getLocation());


            hologram.appendTextLine(machine_name.toString());
        } else {

            TextLine line = (TextLine) hologram.getLine(0);

            line.setText(machine_name.toString());

        }

    }

    /**
     * Deletes the machine hologram
     */
    public void deleteHologram() {

        hologram.delete();

    }

    public void incrementAmount() {
        this.amount++;

        this.updateName();
    }

    public boolean decrementAmount() {

        this.amount--;

        this.updateName();

        return this.amount <= 0;
    }

    public void destroy(Player p) {

        deleteHologram();

        this.getMachineLocation().getLocation().getBlock().setType(Material.AIR);

        p.getInventory().addItem(getItem());

    }

    public ItemStack getItem() {
        Map<String, String> formats = new HashMap<>();

        MachineConfiguration configuration = this.getConfiguration();
        formats.put("%baseAmount%", String.valueOf(configuration.getBaseAmount()));
        formats.put("%price%", String.valueOf(configuration.getPrice()));
        formats.put("%spacing%", DurationFormatUtils.formatDuration(configuration.getSpacing(), "mm:ss"));

        return ItemUtils.formatItem(Main.getIns().getMachineManager().getStatsItem(), formats);
    }

    /**
     * Tick this machine.
     * <p>
     * This should be run async
     */
    public void checkTick() {

        switch (this.getConfiguration().getType()) {

            case ALWAYS_RUNNING: {
                tick();

                break;
            }
            case WHEN_LOADED: {
                if (this.getMachineLocation().getLocation().getChunk().isLoaded()) {
                    tick();
                }
                break;
            }
            case WHEN_ONLINE: {

                PlayerData player = MainData.getIns().getPlayerManager().getPlayer(this.owner);

                if (player != null && player.isPlayerOnServer()) {
                    tick();
                }

                break;
            }
            default:
                break;
        }

    }

    private void tick() {

        currentSpacing--;

        if (currentSpacing == 0) {
            giveReward();
            this.currentSpacing = this.getConfiguration().getSpacing();
        }

    }

    private void giveReward() {

        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(this.owner);
        PlayerData player;

        if (data.getKey() == null) {
            return;
        }

        //Assert that the player's server data has been loaded
        if (data.getValue()) {
            player = MainData.getIns().getPlayerManager().requestAditionalServerData(data.getKey());
        } else {
            player = data.getKey();
        }

        MainData.getIns().getServerCurrencyHandler().addCurrency(data.getKey(), this.getConfiguration().getBaseAmount() * this.amount);

        if (player instanceof MachinePlayer && data.getValue()) {

            MachinePlayer machinePlayer = (MachinePlayer) player;

            machinePlayer.setAmountMadeWhileAway(machinePlayer.getAmountMadeWhileAway() + (getConfiguration().getBaseAmount() * amount));

        } else if (!data.getValue()) {

            //Player is online, send message
            MainData.getIns().getMessageManager().getMessage("MACHINE_MONEY")
                    .format("%amount%", getConfiguration().getBaseAmount() * amount)
                    .sendTo(player);
        } else if (data.getValue()) {

            player.save((c) -> {
            });

        }
    }

    /**
     * Get the machine from the information stored in the NBT
     *
     * @param item
     * @return
     */
    public static Machine getMachineFromItem(ItemStack item) {

        NBTCompound compound = new NBTCompound(item);

        if (compound.getValues().containsKey("MachineID")) {
            return Main.getIns().getMachineManager().getMachineWithID(UUID.fromString((String) compound.getValues().get("MachineID")));
        }

        return null;
    }

    /**
     * Write the item
     * @param item
     * @return
     */
    public ItemStack writeToItem(ItemStack item) {
        NBTCompound compound = new NBTCompound(item);

        compound.add("MachineID", getMachineID().toString());

        return compound.write(item);
    }

    public enum MachineType {

        ALWAYS_RUNNING,
        WHEN_LOADED,
        WHEN_ONLINE

    }

}