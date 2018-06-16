package com.nuno1212s.multipliers.multipliers;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Rank multipliers
 */
public class RankManager {

    private List<RankMultiplier> rankMultipliers;

    @Getter
    private RankMultiplier globalMultiplier;

    private File dataFolder, globalFile;

    public RankManager(Module m) {
        rankMultipliers = new ArrayList<>();

        dataFolder = new File(m.getDataFolder() + File.separator + "Multipliers" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.globalFile = m.getFile("rankmultipliers.json", true);

        reload();
    }

    public void reload() {
        this.globalMultiplier = new RankMultiplier(globalFile);

        for (File file : dataFolder.listFiles()) {
            this.rankMultipliers.add(new RankMultiplier(file));
        }
    }

    public RankMultiplier getRankMultiplier(String id) {
        for (RankMultiplier rankMultiplier : rankMultipliers) {
            if (rankMultiplier.getId().equalsIgnoreCase(id)) {
                return rankMultiplier;
            }
        }
        return null;
    }

}
