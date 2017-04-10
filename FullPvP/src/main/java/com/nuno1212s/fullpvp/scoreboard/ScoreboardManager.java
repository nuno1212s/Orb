package com.nuno1212s.fullpvp.scoreboard;

import com.nuno1212s.fullpvp.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles scoreboard creation
 */
public class ScoreboardManager {

    private Map<UUID, SimpleScoreboard> scoreboards;

    private Map<Integer, String> scoreboardMessages;

    public ScoreboardManager(File f) {
        scoreboards = new HashMap<>();
        scoreboardMessages = new HashMap<>();

        JSONObject json;

        try (Reader r = new FileReader(f)){
            json = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        Map<String, Object> scoreboardMessages = (Map<String, Object>) json;

        scoreboardMessages.entrySet().forEach((entry) -> {
            this.scoreboardMessages.put(Integer.parseInt(entry.getKey()), ChatColor.translateAlternateColorCodes('&', (String) entry.getValue()));
        });

    }

    public void createScoreboard(PVPPlayerData d, Player p) {
        SimpleScoreboard sc;

        if (this.scoreboards.containsKey(d.getPlayerID())) {
            sc = this.scoreboards.get(d.getPlayerID());
        } else {
            sc = new SimpleScoreboard(this.scoreboardMessages.get(-1));
        }

        this.scoreboardMessages.forEach((scoreboardKey, message) -> {
            if (scoreboardKey == -1) {
                return;
            }

            sc.add(format(message, d), scoreboardKey);

        });

        sc.update();
        sc.send(p);

        this.scoreboards.put(d.getPlayerID(), sc);

        setScoreboardPrefixes(d);
    }

    private void setScoreboardPrefixes(PVPPlayerData d) {
        Scoreboard b = this.scoreboards.get(d.getPlayerID()).getScoreboard();
        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            if (!(playerData instanceof PVPPlayerData)) {
                continue;
            }
            PVPPlayerData data = (PVPPlayerData) playerData;

            Group representingGroup = data.getRepresentingGroup();

            Team team = b.getTeam(representingGroup.getScoreboardName());

            if (team == null) {
                team = b.registerNewTeam(representingGroup.getScoreboardName());
                team.setPrefix(representingGroup.getGroupPrefix());
                team.setSuffix(representingGroup.getGroupSuffix());
            }

            team.addEntry(data.getPlayerName());

        }
    }

    private String format(String message, PVPPlayerData d) {
        message = message.replace("%coins%", String.valueOf(d.getCoins()));
        message = message.replace("%cash%", String.valueOf(d.getCash()));
        message = message.replace("%group%", String.valueOf(d.getRepresentingGroup().getGroupPrefix()));
        return message;
    }

}
