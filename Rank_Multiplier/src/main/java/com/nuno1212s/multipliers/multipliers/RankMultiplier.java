package com.nuno1212s.multipliers.multipliers;

import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
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
 * Rank multipliers
 */
public class RankMultiplier {

    @Getter
    private String id;

    private Map<Short, Double> rankMultipliers;

    public RankMultiplier(File f) {
        JSONObject jsonObject;

        try (FileReader r = new FileReader(f)) {
            jsonObject = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        id = (String) jsonObject.get("ID");

        rankMultipliers = new HashMap<>();

        JSONObject jsonObject1 = (JSONObject) jsonObject.get("Multipliers");
        for (String s : (Set<String>) jsonObject1.keySet()) {
            rankMultipliers.put(Short.parseShort(s), (Double) jsonObject1.get(s));
        }

    }

    public double getRankMultiplierForPlayer(PlayerData d) {
        return 1 + getRankMultiplierForRank(d.getMainGroup().getGroupID()) + getRankMultiplierForRank(d.getServerGroup());
    }

    public double getRankMultiplierForRank(short rank) {

        if (rankMultipliers.containsKey(rank)) {
            return rankMultipliers.get(rank);
        }

        return 0;
    }

}
