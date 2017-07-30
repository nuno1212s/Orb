package com.nuno1212s.spawners.rewardhandler;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.entity.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reward manager
 */
public class RewardManager {

    @Getter
    private Map<EntityType, Long> rewardPerEntity;

    public RewardManager(Module m) {
        rewardPerEntity = new HashMap<>();

        JSONObject jsonObject;

        try (FileReader r = new FileReader(m.getFile("rewards.json", true))) {

            jsonObject = (JSONObject) new JSONParser().parse(r);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        for (EntityType entityType : EntityType.values()) {
            if (jsonObject.containsKey(entityType.name())) {
                rewardPerEntity.put(entityType, (Long) jsonObject.get(entityType.name()));
            }
        }

    }

    public static enum EntityType {

        COW(Cow.class),
        PIG(Pig.class),
        SHEEP(Sheep.class),
        ZOMBIE(Zombie.class),
        PIG_ZOMBIE(PigZombie.class),
        SPIDER(Spider.class),
        SKELETON(Skeleton.class),
        WITHER_SKELETON(Skeleton.class),
        BLAZE(Blaze.class);

        Class<? extends LivingEntity> entity;

        EntityType(Class<? extends LivingEntity> entity) {
            this.entity = entity;
        }

        public boolean isEntity(LivingEntity e) {
            org.bukkit.entity.EntityType type = e.getType();
            return type.getEntityClass().equals(entity);
        }

        public static EntityType getEntityType(LivingEntity e) {

            for (EntityType entityType : EntityType.values()) {

                if (entityType.isEntity(e)) {
                    return entityType;
                }
            }

            return null;
        }

    }

}
