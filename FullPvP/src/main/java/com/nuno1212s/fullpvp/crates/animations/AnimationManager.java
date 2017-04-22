package com.nuno1212s.fullpvp.crates.animations;

import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.crates.CrateManager;
import com.nuno1212s.fullpvp.crates.animations.AnimationTimer.AnimationTimer;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles all animations
 */
@Getter
public class AnimationManager {

    private List<ItemStack> showItems;
    
    private File animationStuff;

    private AnimationTimer timer;

    private Random random = new Random();

    public AnimationManager(File animationStuff) {
        timer = new AnimationTimer();
        MainData.getIns().getScheduler().runTaskTimer(timer, 0, 10);
        this.animationStuff = animationStuff;
        showItems = new ArrayList<>();

        JSONObject json;

        try (Reader r = new FileReader(this.animationStuff)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            System.out.println("JSON file could not be read. Maybe it's undefined? ");
            return;
        }

        JSONArray displayItems = (JSONArray) json.get("DisplayItems");
        for (Object displayItem : displayItems) {
            if (displayItem instanceof String) {
                try {
                    showItems.add(CrateManager.itemFrom64((String) displayItem));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void save() {
        if (!animationStuff.exists()) {
            try {
                animationStuff.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        List<String> displayItems = new ArrayList<>();
        JSONObject obj = new JSONObject();
        this.showItems.forEach(displayItem ->
            displayItems.add(CrateManager.itemTo64(displayItem))
        );

        obj.put("DisplayItems", displayItems);

        try (Writer w = new FileWriter(animationStuff)) {
            obj.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ItemStack getRandomDisplayItem() {
        return this.showItems.get(random.nextInt(this.showItems.size()));
    }

    public void addDisplayItem(ItemStack item) {
        this.showItems.add(item);
    }

    public void registerAnimation(Animation animation) {
        this.timer.registerAnimation(animation);
    }

    public boolean isInventoryBeingUsed(Inventory i) {
        return this.timer.isInventoryRegistered(i);
    }

    public void cancelAnimation(Player p, Inventory i) {
        Animation animation = this.timer.getAnimation(i);
        this.timer.cancelAnimation(animation);
        Crate crate = animation.getCrate();

        ItemStack randomReward;
        try {
            randomReward = crate.getRandomReward();
        } catch (Exception e) {
            e.printStackTrace();
            MainData.getIns().getMessageManager().getMessage("COULD_NOT_GIVE_REWARD").sendTo(p);
            return;
        }

        p.getInventory().addItem(randomReward);
    }

}
