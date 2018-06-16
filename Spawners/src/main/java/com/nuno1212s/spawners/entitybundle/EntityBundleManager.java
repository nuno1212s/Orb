package com.nuno1212s.spawners.entitybundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.spawners.entitybundle.timers.EntityBundler;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class EntityBundleManager {

    private Map<EntityType, ItemStack[]> entityDrops = new HashMap<>();

    private Map<Integer, Double> lootingModifier = new HashMap<>();

    private List<EntityBundle> entityBundles;

    private File dropsFile, entitiesFile, lootingConfig;

    private EntityBundler entityBundler;

    private Gson gson;

    public EntityBundleManager(Module m) {
        this.dropsFile = m.getFile("drops.json", true);
        this.lootingConfig = m.getFile("lootingConfig.json", true);
        this.entitiesFile = m.getFile("entityBundles.json", false);

        entityBundler = new EntityBundler();

        this.gson = new GsonBuilder().registerTypeAdapter(EntityBundle.class, new EntityBundleTypeAdapter()).create();

        loadLootingConfig();
        loadDrops();
        loadEntities();
    }

    /**
     * Load the entity drops
     */
    public void loadDrops() {
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
        } finally {
            if (this.entityDrops == null) {
                this.entityDrops = new HashMap<>();
            }
        }
    }

    public void loadLootingConfig() {
        try (Reader r = new FileReader(this.lootingConfig)) {

            Type t = new TypeToken<Map<Integer, Double>>(){}.getType();
            this.lootingModifier = this.gson.fromJson(r, t);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (this.lootingModifier == null) {
                this.lootingModifier = new HashMap<>();
            }
        }
    }

    /**
     * Load the entity bundles
     */
    private void loadEntities() {

        try (Reader r = new FileReader(this.entitiesFile)) {

            Type type = new TypeToken<List<EntityBundle>>() {}.getType();

            List<EntityBundle> list = gson.fromJson(r, type);

            if (list == null) {
                this.entityBundles = Collections.synchronizedList(new ArrayList<>());
            } else {
                this.entityBundles = Collections.synchronizedList(list);
            }

        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        } finally {
            if (this.entityBundles == null) {
                this.entityBundles = Collections.synchronizedList(new ArrayList<>());
            }
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

        this.entityBundles.forEach(EntityBundle::unload);

        try (Writer w = new FileWriter(this.entitiesFile)) {

            gson.toJson(this.entityBundles, w);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.entityBundles.forEach(EntityBundle::forceRemove);
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

        synchronized (this.entityBundles) {
            for (EntityBundle entityBundle : this.entityBundles) {
                if (entityBundle.getType() == entity.getType() && entityBundle.isLoaded()) {
                    Entity entityReference = entityBundle.getEntityReference();
                    if (entityReference.getWorld().getName().equalsIgnoreCase(l.getWorld().getName())) {
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
    }

    /**
     * Get a bundle that a given entity owns
     *
     * @param e The entity to search
     * @return
     */
    public EntityBundle getEntityBundle(Entity e) {
        synchronized (this.entityBundles) {
            for (EntityBundle entityBundle : this.entityBundles) {
                if (entityBundle.getEntity().equals(e.getUniqueId())) {
                    return entityBundle;
                }
            }

            return null;
        }
    }

    /**
     * Get all the spawned entity bundles
     *
     * @return
     */
    public List<EntityBundle> getSpawnedEntityBundles() {
        synchronized (this.entityBundles) {
            return this.entityBundles.stream().filter(EntityBundle::isLoaded).collect(Collectors.toList());
        }
    }

    /**
     * Get the entities sorted by the world
     * @return
     */
    public Map<String, List<EntityBundle>> getSpawnedEntitiesByWorld() {

        Map<String, List<EntityBundle>> entities = new HashMap<>();

        synchronized (this.entityBundles) {
            for (EntityBundle spawnedEntityBundle : entityBundles) {
                String WorldName = spawnedEntityBundle.isLoaded() ?
                        spawnedEntityBundle.getEntityReference().getWorld().getName()
                        : spawnedEntityBundle.getSpawnLocation().getWorld();

                List<EntityBundle> orDefault = entities.getOrDefault(WorldName, new ArrayList<>());
                orDefault.add(spawnedEntityBundle);
                entities.put(WorldName, orDefault);
            }
        }

        return entities;
    }

    /**
     * Accept bundled entities
     * @param entitiesByWorld
     */
    public void acceptNewEntities(Map<String, List<EntityBundle>> entitiesByWorld) {
        this.entityBundles.clear();
        for (List<EntityBundle> bundles : entitiesByWorld.values()) {
            this.entityBundles.addAll(bundles);
        }
    }

    /**
     * Get the unspawned entity bundles for a certain chunk
     *
     * @param chunk The chunk in question
     * @return
     */
    public List<EntityBundle> getUnspawnedBundles(Chunk chunk) {
        List<EntityBundle> bundles = new ArrayList<>();

        synchronized (this.entityBundles) {
            for (EntityBundle entityBundle : this.entityBundles) {
                if (entityBundle.getSpawnLocation() != null) {
                    Location location = entityBundle.getSpawnLocation().getLocation();
                    if (location != null && location.getChunk().equals(chunk)) {
                        bundles.add(entityBundle);
                    }
                }
            }
        }


        return bundles;
    }

    /**
     * Handle the death of an entity bundle
     *
     * @param bundle The entity bundle
     * @return True if the entity should die, false if only 1 has been killed
     */
    public boolean handleDeath(EntityBundle bundle) {
        Player killer = ((LivingEntity) bundle.getEntityReference()).getKiller();

        if (killer != null) {
            bundle.kill(killer.getItemInHand());
        } else {
            bundle.kill(new ItemStack(Material.AIR));
        }

        if (bundle.getMobCount() == 0) {
            synchronized (this.entityBundles) {
                this.entityBundles.remove(bundle);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the modifier for the looting enchantment
     *
     * @param lootingLevel The level of the looting enchantment
     * @return
     */
    public double getModifierForLooting(int lootingLevel) {
        return this.lootingModifier.getOrDefault(lootingLevel, 1D);
    }

}
