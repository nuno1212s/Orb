package com.nuno1212s.crates.crates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.crates.TypeAdapter.ItemStackListAdapter;
import com.nuno1212s.crates.animations.AnimationManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.typeadapters.ItemStackTypeAdapter;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Handles crates
 */
public class CrateManager {

    @Getter
    AnimationManager animationManager;

    private List<Crate> crates;

    private File crateFile, crateLocationFile;

    private Map<String, List<LLocation>> crateBlocks;

    @Getter
    private Gson gson;

    @Getter
    @Setter
    private ItemStack defaultKeyItem;

    @Getter
    private InventoryData confirmInventory, crateDisplayInventory;

    @SuppressWarnings("unchecked")
    public CrateManager(Module mainModule) {

        this.gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter())
                .registerTypeAdapter(LLocation.class, new LocationTypeAdapter())
                //.registerTypeAdapter(new TypeToken<List<ItemStack>>(){}.getType(), new ItemStackListAdapter())
                .create();

        this.defaultKeyItem = new ItemStack(Material.TRIPWIRE_HOOK);

        this.crateFile = mainModule.getFile("crates.json", false);
        this.crateLocationFile = mainModule.getFile("crateLocations.json", false);
        this.animationManager = new AnimationManager(mainModule.getFile("animationFile.json", false));
        this.confirmInventory = new InventoryData(mainModule.getFile("confirmInventory.json", true));
        this.crateDisplayInventory = new InventoryData(mainModule.getFile("crateDisplay.json", true));


        Type type = new TypeToken<List<Crate>>() {
        }.getType(),
                type2 = new TypeToken<Map<String, LLocation>>() {
                }.getType();

        try (Reader reader = new FileReader(this.crateFile);
             Reader reader2 = new FileReader(this.crateLocationFile)) {
            this.crates = this.gson.fromJson(reader, type);

            this.crateBlocks = this.gson.fromJson(reader2, type2);

        } catch (IOException | JsonParseException e) {
            System.out.println("JSON file could not be read. Maybe it's undefined? ");
            e.printStackTrace();
        } finally {
            if (this.crates == null) {
                this.crates = new ArrayList<>();
            }

            this.crates.forEach(Crate::recalculateProbabilities);

            if (this.crateBlocks == null) {
                this.crateBlocks = new HashMap<>();
            }
        }


    }

    /**
     * Save crate data
     */
    public void save() {
        animationManager.save();

        try {
            if (!this.crateFile.exists()) {
                this.crateFile.createNewFile();
            }

            if (!this.crateLocationFile.exists()) {
                this.crateLocationFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Writer crateFileWriter = new FileWriter(this.crateFile);
             Writer crateLocationWriter = new FileWriter(this.crateLocationFile)) {
            this.gson.toJson(this.crates, crateFileWriter);

            this.gson.toJson(this.crateBlocks, crateLocationWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get a crate linked to a block at a certain location
     *
     * @param l
     * @return
     */
    public Crate getCrateAtLocation(Location l) {
        for (Map.Entry<String, List<LLocation>> crateLocation : this.crateBlocks.entrySet()) {
            List<LLocation> locationss = crateLocation.getValue();
            for (LLocation locations : locationss) {
                if (locations.getWorld().equalsIgnoreCase(l.getWorld().getName())) {
                    if (locations.getLocation().distanceSquared(l) < 2) {
                        return getCrate(crateLocation.getKey());
                    }
                }
            }
        }

        return null;
    }

    /**
     * Is the location given a location of a crate block
     *
     * @param l
     * @return
     */
    public boolean isCrateLocation(Location l) {
        for (Map.Entry<String, List<LLocation>> crateLocation : this.crateBlocks.entrySet()) {
            List<LLocation> locationss = crateLocation.getValue();
            for (LLocation locations : locationss) {
                if (locations.getWorld().equalsIgnoreCase(l.getWorld().getName())) {
                    if (locations.getLocation().distanceSquared(l) < 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void handleCrateRemoval(Crate c) {
        for (Map.Entry<String, List<LLocation>> crateLocation : this.crateBlocks.entrySet()) {
            if (crateLocation.getKey().equalsIgnoreCase(c.getCrateName())) {
                this.crateBlocks.remove(crateLocation.getKey());
            }
        }
    }

    public void setCrateAtLocation(Location l, Crate c) {
        if (isCrateLocation(l)) {
            return;
        }

        LLocation lLocation = new LLocation(l);

        List<LLocation> orDefault = this.crateBlocks.getOrDefault(c.getCrateName(), new ArrayList<>());

        orDefault.add(lLocation);

        this.crateBlocks.put(c.getCrateName(), orDefault);
    }

    public void removeCrateAtLocation(Location l) {
        if (isCrateLocation(l)) {
            this.crateBlocks.remove(new LLocation(l));
        }
    }

    public boolean canOpen(Player p, Crate crateToOpen) {
        PlayerInventory inventory = p.getInventory();
        ListIterator<ItemStack> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (crateToOpen.checkIsKey(itemStack)) {

                if (itemStack.getAmount() > 1) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                } else {
                    iterator.set(new ItemStack(Material.AIR));
                }

                return true;
            }
        }
        return false;
    }

    public void addCrate(Crate c) {
        this.crates.add(c);
    }

    public void removeCrate(Crate c) {
        this.crates.remove(c);
        handleCrateRemoval(c);
    }

    public Crate getCrate(String crateName) {
        for (Crate crate : this.crates) {
            if (crate.getCrateName().equalsIgnoreCase(crateName)) {
                return crate;
            }
        }
        return null;
    }

    public Crate getCrateForKey(ItemStack item) {
        for (Crate crate : this.crates) {
            if (crate.checkIsKey(item)) {
                return crate;
            }
        }
        return null;
    }

    public boolean isCrateKey(ItemStack item) {
        for (Crate crate : this.crates) {
            if (crate.checkIsKey(item)) {
                return true;
            }
        }
        return false;
    }

    public static String itemTo64(ItemStack stack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);

            // Serialize that array

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack itemFrom64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            try {
                return (ItemStack) dataInput.readObject();
            } finally {
                dataInput.close();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
