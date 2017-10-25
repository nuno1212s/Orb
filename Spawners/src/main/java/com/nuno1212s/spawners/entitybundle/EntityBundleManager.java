package com.nuno1212s.spawners.entitybundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.SerializableItem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBundleManager {

    private Map<EntityType, ItemStack[]> entityDrops = new HashMap<>();

    private List<EntityBundle> entityBundles;

    private File dropsFile, entitiesFile;

    private Gson gson;

    public EntityBundleManager(Module m) {
        this.dropsFile = m.getFile("drops.json", true);
        this.entitiesFile = m.getFile("entityBundles.json", false);
        this.gson = new GsonBuilder().registerTypeAdapter(EntityBundle.class, new EntityBundleTypeAdapter()).create();

        loadDrops();
        loadEntities();
    }

    /**
     * Load the entity drops
     */
    private void loadDrops() {
        this.entityDrops = new HashMap<>();

        try (Reader r = new FileReader(dropsFile)) {

            JSONObject drops = (JSONObject) new JSONParser().parse(r);

            drops.forEach((entityType, itemDrops) -> {
                EntityType type = EntityType.valueOf((String) entityType);

                JSONArray itemArray = (JSONArray) itemDrops;

                ItemStack[] items = new ItemStack[itemArray.size()];

                int i = 0;
                for (JSONObject item : (List<JSONObject>) itemArray) {
                    items[i++] = new SerializableItem(item);
                }

                entityDrops.put(type, items);
            });

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the entity bundles
     */
    private void loadEntities() {

        try (Reader r = new FileReader(this.entitiesFile)) {

            Type type = new TypeToken<List<EntityBundle>>() {}.getType();

            this.entityBundles = gson.fromJson(r, type);

        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        } finally {
            this.entityBundles = new ArrayList<>();
        }
    }

    /**
     * Save the entity bundles to a file
     */
    public void saveEntities() {
        if (!this.entitiesFile.exists()) {
            try {
                this.entitiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Writer w = new FileWriter(this.entitiesFile)) {

            gson.toJson(this.entityBundles, w);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.entityBundles.forEach(EntityBundle::remove);
        }
    }

    /**
     * Get the drops for a given entity
     *
     * @param type The type of entity
     * @return
     */
    public ItemStack[] getDropsForEntity(EntityType type) {
        return this.entityDrops.getOrDefault(type, new ItemStack[0]);
    }

    /**
     * Get the nearest Entity bundle to an entity
     *
     * @param entity    The entity to find a bundle for
     * @param maxRadius The max radius to search in
     * @return
     */
    public EntityBundle getNearestEntityBundleTo(Entity entity, double maxRadius) {
        maxRadius *= maxRadius;
        Location l = entity.getLocation();

        for (EntityBundle entityBundle : this.entityBundles) {
            if (entityBundle.getType() == entity.getType() && entityBundle.isLoaded()) {
                Entity entityReference = entityBundle.getEntityReference();
                if (entityReference.getName().equalsIgnoreCase(l.getWorld().getName())) {
                    if (entityReference.getLocation().distanceSquared(l) < maxRadius) {
                        return entityBundle;
                    }
                }
            }
        }

        EntityBundle entityBundle = new EntityBundle(entity.getType(), entity.getLocation().clone());

        this.entityBundles.add(entityBundle);

        return entityBundle;
    }

    /**
     * Get a bundle that a given entity owns
     *
     * @param e The entity to search
     * @return
     */
    public EntityBundle getEntityBundle(Entity e) {
        for (EntityBundle entityBundle : this.entityBundles) {
            if (entityBundle.getEntity().equals(e.getUniqueId())) {
                return entityBundle;
            }
        }

        return null;
    }

    public List<EntityBundle> getUnspawnedBundles(Chunk chunk) {
        List<EntityBundle> bundles = new ArrayList<>();

        for (EntityBundle entityBundle : this.entityBundles) {
            if (entityBundle.getSpawnLocation() != null) {
                Location location = entityBundle.getSpawnLocation().getLocation();
                if (location != null && location.getChunk().equals(chunk)) {
                    bundles.add(entityBundle);
                }
            }
        }

        return bundles;
    }

}
