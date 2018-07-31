package com.nuno1212s.rankup.skillvisualizer;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SkillInventory extends InventoryData<SkillItem> {

    public SkillInventory(File file) {
        super(file, SkillItem.class, true);

        setOpenFuction((data) -> {

            HumanEntity player = data.getKey();

            InventoryData value = data.getValue();

            if (value instanceof SkillInventory) {

                ((SkillInventory) value).buildInventoryAsync((Player) player).thenAccept((inv) -> {
                    if (((Player) player).isOnline()) {
                        player.openInventory(inv);
                    }
                });

            } else{
                player.openInventory(value.buildInventory());
            }

        });
    }

    public CompletableFuture<Inventory> buildInventoryAsync(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            Inventory i = Bukkit.getServer().createInventory(null, getInventorySize(), getInventoryName());

            for (SkillItem item : this.items) {

                if (!item.hasItemFlag("TOP")) {
                    i.setItem(item.getSlot(), item.getItem(p));
                } else {
                    i.setItem(item.getSlot(), item.getItem());
                }

            }

            return i;
        });
    }

}

class SkillItem extends InventoryItem {

    String mcMmoSkill;

    public SkillItem(JSONObject jsonData) {
        super(jsonData);

        mcMmoSkill = (String) jsonData.getOrDefault("Skill", null);
    }


    public ItemStack getItem() {

        ItemStack clone = this.item.clone();

        Map<String, String> formats = new HashMap<>();

        if (hasItemFlag("TOP")) {
            if (mcMmoSkill != null) {

                List<PlayerStat> playerStats = mcMMO.getDatabaseManager().readLeaderboard(SkillType.valueOf(mcMmoSkill), 1, 5);

                for (int i = 0; i < 4; i++) {
                    PlayerStat playerStat = playerStats.get(i);

                    formats.put("%player" + String.valueOf(i + 1) + "%", playerStat.name);
                    formats.put("%score" + String.valueOf(i + 1) + "%", String.valueOf(playerStat.statVal));

                }

            } else {

                List<PlayerStat> playerStats = mcMMO.getDatabaseManager().readLeaderboard(null, 1, 5);

                for (int i = 0; i < 4; i++) {
                    PlayerStat playerStat = playerStats.get(i);

                    formats.put("%player" + String.valueOf(i + 1) + "%", playerStat.name);
                    formats.put("%score" + String.valueOf(i + 1) + "%", String.valueOf(playerStat.statVal));
                }
            }

        } else {
            return super.getItem();
        }

        return ItemUtils.formatItem(clone, formats);
    }

    public ItemStack getItem(Player player) {

        ItemStack clone = this.item.clone();

        Map<String, String> formats = new HashMap<>();

        formats.put("%playerName%", player.getName());

        if (clone.getType() == Material.SKULL_ITEM) {
            SkullMeta itemMeta = (SkullMeta) clone.getItemMeta();

            itemMeta.setOwner(player.getName());

            clone.setItemMeta(itemMeta);
        }

        if (mcMmoSkill != null) {

            formats.put("%skill%", mcMmoSkill);
            int level = ExperienceAPI.getLevel(player, mcMmoSkill);
            formats.put("%skillLevel%", String.valueOf(level));
            formats.put("%skillXP%", String.valueOf(ExperienceAPI.getXP(player, mcMmoSkill)));
            formats.put("%skillXPToNextLevel%", String.valueOf(ExperienceAPI.getXPRemaining(player, mcMmoSkill)));
            formats.put("%skillXPNeeded%", String.valueOf(ExperienceAPI.getXpNeededToLevel(level, mcMmoSkill)));
            formats.put("%skillRank%", String.valueOf(mcMMO.getDatabaseManager().readRank(player.getName()).get(SkillType.valueOf(mcMmoSkill))));

        } else {

            formats.put("%level%", String.valueOf(ExperienceAPI.getPowerLevel(player)));
            formats.put("%rank%", String.valueOf(mcMMO.getDatabaseManager().readRank(player.getName()).get(null)));

        }

        return ItemUtils.formatItem(clone, formats);
    }

}