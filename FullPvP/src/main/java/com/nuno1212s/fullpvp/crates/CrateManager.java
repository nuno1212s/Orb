package com.nuno1212s.fullpvp.crates;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Handles crates
 */
public class CrateManager {

    @Getter
    AnimationManager animationManager;

    private List<Crate> crates;

    private File crateFile;

    @SuppressWarnings("unchecked")
    public CrateManager(Module mainModule) {

        this.crates = new ArrayList<>();
        this.crateFile = mainModule.getFile("crates.json", false);

        JSONObject obj;

        try (Reader reader = new FileReader(this.crateFile)) {
            obj = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("JSON file could not be read. Maybe it's undefined? ");
            return;
        }

        Map<String, Object> crates = (JSONObject) obj.get("Crates");

        crates.keySet().forEach(crate -> {
            List<Map<String, Object>> rewards = (List<Map<String, Object>>) crates.get(crate);
            List<Reward> rewardList = new ArrayList<>();
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
            this.crates.add(new Crate(crate, rewardList));
        });

    }

    public void save() {
        animationManager.save();
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

    static String itemTo64(ItemStack stack) throws IllegalStateException {
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

    static ItemStack itemFrom64(String data) throws IOException {
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
