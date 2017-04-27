package com.nuno1212s.fullpvp.crates;

import com.nuno1212s.fullpvp.crates.animations.AnimationManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Handles crates
 */
public class CrateManager {

    @Getter
    AnimationManager animationManager;

    private List<Crate> crates;

    private File crateFile;

    private Map<Location, Crate> crateBlocks;

    @Getter
    @Setter
    private ItemStack defaultKeyItem;

    @SuppressWarnings("unchecked")
    public CrateManager(Module mainModule) {

        this.defaultKeyItem = new ItemStack(Material.BONE);
        this.crateBlocks = new HashMap<>();
        this.crates = new ArrayList<>();
        this.crateFile = mainModule.getFile("crates.json", false);
        this.animationManager = new AnimationManager(mainModule.getFile("animationFile.json", false));

        JSONObject obj;

        try (Reader reader = new FileReader(this.crateFile)) {
            obj = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("JSON file could not be read. Maybe it's undefined? ");
            return;
        }

        Map<String, Object> crates = (JSONObject) obj.get("Crates");

        crates.keySet().forEach(crate -> {
            Map<String, Object> crateStuff = (Map<String, Object>) crates.get(crate);
            List<Map<String, Object>> rewards = (List<Map<String, Object>>) crateStuff.get("Rewards");
            String displayName = (String) crateStuff.get("DisplayName");
            boolean cash = (boolean) crateStuff.get("IsCash");
            long price = (long) crateStuff.get("Price");
            Set<Reward> rewardList = new HashSet<>();

            rewards.forEach(reward -> {
                int percentage = ((Long) reward.get("Percentage")).intValue();
                int rewardID = ((Long) reward.get("RewardID")).intValue();
                ItemStack item;
                try {
                    item = itemFrom64((String) reward.get("Item"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                rewardList.add(new Reward(rewardID, item, percentage));
            });
            this.crates.add(new Crate(crate, displayName, rewardList, cash, price));
        });

        List<Map<String, Object>> crateBlocks = (List<Map<String, Object>>) obj.get("CrateLocations");

        crateBlocks.forEach(crateLocation -> {
            String location = (String) crateLocation.get("Location");
            String crateName = (String) crateLocation.get("CrateName");
            SerializableLocation loc = new SerializableLocation(location);
            Crate c = getCrate(crateName);
            this.crateBlocks.put(loc, c);
        });

        String defaultKeyItem = (String) obj.get("DefaultKeyItem");
        try {
            this.defaultKeyItem = itemFrom64(defaultKeyItem);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save crate data
     */
    public void save() {
        animationManager.save();

        if (!crateFile.exists()) {
            try {
                crateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject obj = new JSONObject(), crates = new JSONObject();

        JSONArray crateLocations = new JSONArray();

        this.crates.forEach(crate -> {
            JSONObject crateStuff = new JSONObject();
            JSONArray rewards = new JSONArray();

            crate.getRewards().forEach(reward -> {
                JSONObject rewardObject = new JSONObject();
                rewardObject.put("Percentage", (int) reward.getOriginalProbability());
                rewardObject.put("RewardID", reward.getRewardID());
                rewardObject.put("Item", itemTo64(reward.getItem()));
                rewards.add(rewardObject);
            });
            crateStuff.put("Rewards", rewards);
            crateStuff.put("DisplayName", crate.getDisplayName());
            crateStuff.put("IsCash", crate.isCash());
            crateStuff.put("Price", crate.getKeyCost());
            crates.put(crate.getCrateName(), crateStuff);
        });

        this.crateBlocks.forEach((location, crate) -> {
            SerializableLocation serializableLocation;

            if (!(location instanceof SerializableLocation)) {
                serializableLocation = new SerializableLocation(location);
            } else {
                serializableLocation = (SerializableLocation) location;
            }

            String s = serializableLocation.toString();
            JSONObject object = new JSONObject();
            object.put("Location", s);
            object.put("CrateName", crate.getCrateName());
            crateLocations.add(object);
        });

        obj.put("Crates", crates);
        obj.put("CrateLocations", crateLocations);
        obj.put("DefaultKeyItem", itemTo64(this.defaultKeyItem));

        try (Writer w = new FileWriter(this.crateFile)) {
            obj.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Crate getCrateAtLocation(Location l) {
        for (Map.Entry<Location, Crate> crateLocation : this.crateBlocks.entrySet()) {
            Location locations = crateLocation.getKey();
            if (locations.getBlockX() == l.getBlockX() && locations.getBlockY() == l.getBlockY() && locations.getBlockZ() == l.getBlockZ()) {
                return crateLocation.getValue();
            }
        }
        return null;
    }

    public boolean isCrateLocation(Location l) {
        for (Map.Entry<Location, Crate> crateLocation : this.crateBlocks.entrySet()) {
            Location locations = crateLocation.getKey();
            if (locations.getBlockX() == l.getBlockX() && locations.getBlockY() == l.getBlockY() && locations.getBlockZ() == l.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public void setCrateAtLocation(Location l, Crate c) {
        if (isCrateLocation(l)) {
            return;
        }
        this.crateBlocks.put(l, c);
    }

    public void removeCrateAtLocation(Location l) {
        if (isCrateLocation(l)) {
            this.crateBlocks.remove(l);
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
                    inventory.removeItem(itemStack);
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
    }

    public Crate getCrate(String crateName) {
        for (Crate crate : this.crates) {
            if (crate.getCrateName().equalsIgnoreCase(crateName)) {
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
