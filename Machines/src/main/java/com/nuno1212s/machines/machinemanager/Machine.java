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

    public Machine(UUID owner, MachineConfiguration machineConfiguration, LLocation location, int amount) {
        this.machineID = UUID.randomUUID();
        this.amount = amount;
        this.owner = owner;
        this.configuration = machineConfiguration.getId();
        this.machineLocation = location;
        this.currentSpacing = machineConfiguration.getSpacing() / 50L;

        updateName();
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

        if (configuration == null) {
            return;
        }

        machine_name.format("%name%", configuration.getName());
        machine_name.format("%amount%", this.amount);
        machine_name.format("%baseAmount%", configuration.getBaseAmount());
        machine_name.format("%currentAmount%", configuration.getBaseAmount() * this.amount);
        machine_name.format("%timeDifference%", DurationFormatUtils.formatDuration(configuration.getSpacing(), "mm:ss"));
        machine_name.format("%untilNextTime%", DurationFormatUtils.formatDuration(currentSpacing * 50, "mm:ss"));
        machine_name.format("%machineID%", this.machineID.toString());

        String s = machine_name.toString();

        String[] lines = s.split("\n");

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(BukkitMain.getIns(), this.getMachineLocation().getLocation().add(0.5, 1.1f
                    + (lines.length * (.3f)), 0.5));

            for (String line : lines) {
                hologram.appendTextLine(line);
            }
        } else {

            for (int i = 0; i < lines.length; i++) {

                TextLine line = (TextLine) hologram.getLine(i);

                line.setText(lines[i]);
            }

        }

    }

    /**
     * Deletes the machine hologram
     */
    public void deleteHologram() {

        hologram.delete();

    }

    public void incrementAmount() {
        incrementAmount(1);
    }

    public void incrementAmount(int amount) {

        this.amount += amount;

        this.updateName();

    }


    public void destroy(Player p) {

        deleteHologram();

        this.getMachineLocation().getLocation().getBlock().setType(Material.AIR);

        p.getInventory().addItem(getItemToDrop());

    }

    public ItemStack getItemToDrop() {
        return getConfiguration().getItem(this.amount);
    }

    public ItemStack getItem() {
        Map<String, String> formats = new HashMap<>();

        MachineConfiguration configuration = this.getConfiguration();
        formats.put("%baseAmount%", String.valueOf(configuration.getBaseAmount()));
        formats.put("%price%", String.valueOf(configuration.getPrice()));
        formats.put("%currentAmount%", String.valueOf(configuration.getBaseAmount() * this.amount));
        formats.put("%spacing%", DurationFormatUtils.formatDuration(configuration.getSpacing(), "mm:ss"));
        formats.put("%name%", configuration.getName());
        formats.put("%amount%", String.valueOf(this.amount));

        return ItemUtils.formatItem(Main.getIns().getMachineManager().getStatsItem().clone(), formats);
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

        if ((currentSpacing) % 20 == 0) {
            this.updateName();
        }

        if (currentSpacing <= 0) {
            giveReward();
            this.currentSpacing = this.getConfiguration().getSpacing() / 50;
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

        MachineConfiguration configuration = this.getConfiguration();

        MainData.getIns().getServerCurrencyHandler().addCurrency(data.getKey(), configuration.getBaseAmount() * this.amount);

        if (player instanceof MachinePlayer && data.getValue()) {

            MachinePlayer machinePlayer = (MachinePlayer) player;

            machinePlayer.setAmountMadeWhileAway(machinePlayer.getAmountMadeWhileAway() + (configuration.getBaseAmount() * amount));

        } else if (!data.getValue()) {

            //Player is online, send message
            MainData.getIns().getMessageManager().getMessage("MACHINE_MONEY")
                    .format("%amount%", configuration.getBaseAmount() * amount)
                    .format("%name%", configuration.getName())
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