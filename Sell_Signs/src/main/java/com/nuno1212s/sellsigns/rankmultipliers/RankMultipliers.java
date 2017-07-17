package com.nuno1212s.sellsigns.rankmultipliers;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles rank multipliers
 */
public class RankMultipliers {

    private Map<Short, Double> rankMultipliers;

    public RankMultipliers(Module m) {
        rankMultipliers = new HashMap<>();
        File f = m.getFile("rankmultipliers.json", true);
        JSONObject jS;

        try (FileReader r = new FileReader(f)) {
            jS = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        for (String s : (Set<String>) jS.keySet()) {
            rankMultipliers.put(Short.parseShort(s), (Double) jS.get(s));
        }
    }

    public double getRankMultiplier(PlayerData d) {
        return 1 + getRankMultiplier(d.getMainGroup().getGroupID()) + getRankMultiplier(d.getServerGroup());
    }

    public double getRankMultiplier(short groupID) {
        return rankMultipliers.get(groupID);
    }

}
