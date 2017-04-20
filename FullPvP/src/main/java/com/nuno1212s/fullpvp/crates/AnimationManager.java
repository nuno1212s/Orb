package com.nuno1212s.fullpvp.crates;

import com.nuno1212s.fullpvp.crates.animations.Animation;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all animations
 */
@Getter
public class AnimationManager {

    List<ItemStack> showItems;
    
    File animationStuff;

    public AnimationManager(File animationStuff) {
        this.animationStuff = animationStuff;
        showItems = new ArrayList<>();

        JSONObject json;

        try (Reader r = new FileReader(this.animationStuff)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
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

    public void registerAnimation(Animation animation) {

    }

}
