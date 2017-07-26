package com.nuno1212s.rankup.rankup;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;


public class RankUpManager {

    private short[] rankUp;
    private int[] cost;

    public RankUpManager(Module m) {
        File f = m.getFile("rankup.yml", true);
        YamlConfiguration fc = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection rankUps = fc.getConfigurationSection("RankUp");
        Set<String> ranks = rankUps.getKeys(false);
        this.rankUp = new short[ranks.size()];
        cost = new int[ranks.size()];
        int i = 0;
        for (String s : ranks) {
            Group group = MainData.getIns().getPermissionManager().getGroup(Short.parseShort(s));
            if (group == null) {
                throw new NullPointerException("Group not found");
            }
            this.rankUp[i] = group.getGroupID();
            this.cost[i] = rankUps.getInt(s);
            i++;
        }

    }

    public int getGroupCost(short groupID) {
        int currentRank = 0;
        for (short groupId : rankUp) {
            if (groupId == groupID) {
                break;
            }
            currentRank++;
        }
        return cost.length <= currentRank ? -1 : cost[currentRank];
    }

    public short getNextGroup(short groupID) {
        int currentRank = 0;
        for (short groupId : rankUp) {
            if (groupId == groupID) {
                break;
            }
            currentRank++;
        }
        return rankUp.length <= currentRank + 1 ? -1 : rankUp[currentRank + 1];
    }

    public String getProgression(RUPlayerData d) {
        short nextGroup = getNextGroup(d.getServerGroup());

        if (nextGroup == -1) {
            return "N/A";
        }

        long current = d.getCoins(), needed = getGroupCost(nextGroup);

        int amountOfSteps = 10, currentProgression = (int) (current * amountOfSteps / needed);

        if (currentProgression >= 10) {
            return ChatColor.YELLOW.toString() + "Já pode evoluir";
        } else {

            StringBuilder b = new StringBuilder(ChatColor.WHITE + "Progressão: " + ChatColor.GREEN.toString());

            for (int i = 0; i < currentProgression; i++) {
                b.append("|");
            }

            b.append(ChatColor.GRAY.toString());

            for (int i = currentProgression; i < amountOfSteps; i++) {
                b.append("|");
            }

            return b.toString();
        }
    }

}
