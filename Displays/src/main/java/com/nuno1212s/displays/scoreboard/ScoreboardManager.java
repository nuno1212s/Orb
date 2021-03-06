package com.nuno1212s.displays.scoreboard;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.SimpleScoreboard;
import org.bukkit.Bukkit;
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
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles scoreboard creation
 */
public class ScoreboardManager {

    private Map<UUID, SimpleScoreboard> scoreboards;

    private Map<Integer, String> scoreboardMessages;

    public ScoreboardManager(File f) {
        scoreboards = new ConcurrentHashMap<>();
        scoreboardMessages = new HashMap<>();

        JSONObject json;

        try (Reader r = new FileReader(f)){
            json = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        Map<String, Object> scoreboardMessages = (Map<String, Object>) json;

        scoreboardMessages.forEach((entry, value) ->
            this.scoreboardMessages.put(Integer.parseInt(entry), ChatColor.translateAlternateColorCodes('&', (String) value))
        );

    }

    public void handlePlayerJoin(PlayerData d, Player p) {
        createScoreboard(d, p);
        setScoreboardPrefixes(d);
    }

    public void handlePlayerDC(PlayerData d) {
        this.scoreboards.forEach(((uuid, simpleScoreboard) -> {
            if (uuid.equals(d.getPlayerID())) {
                return;
            }
            Scoreboard scoreboard = simpleScoreboard.getScoreboard();

            Team team = scoreboard.getTeam(d.getRepresentingGroup().getScoreboardName());

            if (team == null) {
                return;
            }

            team.removeEntry(d.getPlayerName());
        }));

        this.scoreboards.remove(d.getPlayerID());
    }

    public void handlePlayerGroupUpdate(PlayerData d, Group oldGroup) {

        this.setScoreboardPrefixes(d);

        /*
        this.scoreboards.forEach(((uuid, simpleScoreboard) -> {

            Scoreboard scoreboard = simpleScoreboard.getScoreboard();

            Team team = scoreboard.getTeam(oldGroup.getScoreboardName());

            if (team == null) {
                return;
            }

            team.removeEntry(d.getPlayerName());

            Group representingGroup = d.getRepresentingGroup();
            Team newGroup = scoreboard.getTeam(representingGroup.getScoreboardName());


            if (newGroup == null) {
                newGroup = scoreboard.registerNewTeam(representingGroup.getScoreboardName());
                newGroup.setPrefix(representingGroup.getGroupPrefix());
                newGroup.setSuffix(representingGroup.getGroupSuffix());
            }

            newGroup.addEntry(d.getPlayerName());
        }));*/

    }

    public void createScoreboard(PlayerData d, Player p) {
        SimpleScoreboard sc;
        // TODO: 28/10/2017 Maybe move this to async?

        if (this.scoreboards.containsKey(d.getPlayerID())) {
            sc = this.scoreboards.get(d.getPlayerID());
        } else {
            sc = new SimpleScoreboard(this.scoreboardMessages.get(-1));
            this.scoreboards.put(d.getPlayerID(), sc);
        }

        this.scoreboardMessages.forEach((scoreboardKey, message) -> {

            if (scoreboardKey == -1) {
                return;
            }

            String substring = format(message, d);

            if (substring.length() >= 31) {
                substring = substring.substring(0, 31);
            }

            sc.add(substring, scoreboardKey);

        });

        sc.update();
        sc.send(p);

    }

    public void setScoreboardPrefixes(PlayerData d) {
        Scoreboard b = this.scoreboards.get(d.getPlayerID()).getScoreboard();

        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            Group representingGroup = playerData.getRepresentingGroup();

            for (Team t : b.getTeams()) {
                if (t.getEntries().contains(playerData.getPlayerName())) {

                    if (t.getDisplayName().equalsIgnoreCase(representingGroup.getScoreboardName())) break;

                    t.removeEntry(playerData.getPlayerName());
                }
            }

            Team team = b.getTeam(representingGroup.getScoreboardName());

            if (team == null) {
                team = b.registerNewTeam(representingGroup.getScoreboardName());
                team.setPrefix(representingGroup.getGroupPrefix());
                team.setSuffix(representingGroup.getGroupSuffix());
            }

            team.addEntry(playerData.getPlayerName());
        }

        this.scoreboards.forEach(((uuid, simpleScoreboard) -> {
            if (uuid.equals(d.getPlayerID())) {
                return;
            }

            Scoreboard scoreboard = simpleScoreboard.getScoreboard();

            Group representingGroup = d.getRepresentingGroup();
            for (Team team : scoreboard.getTeams()) {
                if (team.getEntries().contains(d.getPlayerName())) {

                    if (team.getName().equalsIgnoreCase(d.getRepresentingGroup().getScoreboardName())) {
                        return;
                    }

                    team.removeEntry(d.getPlayerName());
                    break;
                }
            }

            Team team = scoreboard.getTeam(representingGroup.getScoreboardName());

            if (team == null) {
                team = scoreboard.registerNewTeam(representingGroup.getScoreboardName());
                team.setPrefix(representingGroup.getGroupPrefix());
                team.setSuffix(representingGroup.getGroupSuffix());
            }

            team.addEntry(d.getPlayerName());
        }));

    }

    private String format(String message, PlayerData d) {
        message = message.replace("%cash%", NumberFormat.getInstance().format(d.getCash()));
        message = message.replace("%group%", String.valueOf(d.getRepresentingGroup().getGroupPrefix()));
        message = DisplayMain.getIns().getPlaceHolderManager().format(message, d);
        return message;
    }

}
