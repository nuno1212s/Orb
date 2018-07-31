package com.nuno1212s.rankup.skillvisualizer;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class SkillVisualizer {

    private File skillVisualizer;

    public SkillVisualizer(Module module) {

        this.skillVisualizer = new File(module.getDataFolder() + File.separator + "SkillInventories" + File.separator);

        if (!this.skillVisualizer.exists()) {
            this.skillVisualizer.mkdirs();
        }

        for (File file : this.skillVisualizer.listFiles()) {
            new SkillInventory(file);
        }
    }

    public CompletableFuture<Inventory> getInventory(Player player) {
        return ((SkillInventory) MainData.getIns().getInventoryManager().getInventory("skillVisualizerMain")).buildInventoryAsync(player);
    }

}
