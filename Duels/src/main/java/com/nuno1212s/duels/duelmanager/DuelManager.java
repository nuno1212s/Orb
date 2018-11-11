package com.nuno1212s.duels.duelmanager;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.LLocation;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelManager {

    private List<Duel> onGoingDuels;

    public DuelManager() {

        onGoingDuels = new ArrayList<>();

    }

    /**
     * Get the active duel for a given player
     * @param playerID
     * @return
     */
    public Duel getActiveDuelForPlayer(UUID playerID) {

        for (Duel onGoingDuel : onGoingDuels) {

            if (onGoingDuel.getTeam1().contains(playerID) || onGoingDuel.getTeam2().contains(playerID)) {
                return onGoingDuel;
            }

        }

        return null;
    }

    /**
     * Gets a immutable copy of the current on going duels
     * @return
     */
    public List<Duel> getOnGoingDuels() {

        return ImmutableList.copyOf(this.onGoingDuels);

    }

    public Duel startDuel(UUID player1, UUID player2) {

        Duel d = new Duel(player1, player2);

        //Teleport all the players to the spawns

        this.onGoingDuels.add(d);

        return d;
    }

}
