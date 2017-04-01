package com.nuno1212s.hub.scoreboard;

import com.nuno1212s.core.permissions.PermissionsGroup;
import com.nuno1212s.core.permissions.PermissionsGroupManager;
import com.nuno1212s.core.permissions.PlayerPermissions;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.core.util.SimpleScoreboard;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.servermanager.ServerManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles scoreboard
 */
public class ScoreboardHandler {

    @Getter
    private static ScoreboardHandler ins;

    HashMap<UUID, SimpleScoreboard> board;

    private int lines;

    private HashMap<Integer, String> lineText = new HashMap<>();


    public ScoreboardHandler(Main m) {
        ins = this;
        board = new HashMap<>();
        ConfigurationSection scoreboard = m.getConfig().getConfigurationSection("Scoreboard");

        lines = scoreboard.getInt("Lines");
        ConfigurationSection text = scoreboard.getConfigurationSection("Text");
        Set<String> keys = text.getKeys(false);
        keys.forEach(key ->
                lineText.put(Integer.parseInt(key), ChatColor.translateAlternateColorCodes('&', text.getString(key)))
        );
    }

    public void setupScoreboard(Player p, PlayerData d) {
        SimpleScoreboard s;
        if (!this.board.containsKey(p.getUniqueId())) {
            s = new SimpleScoreboard(this.lineText.get(-1));
        } else {
            s = board.get(p.getUniqueId());
        }
        for (int i = lines; i >= 0; i--) {
            String text = lineText.get(i);
            if (text == null) {
                text = "";
            }
            PlayerData pd = PlayerManager.getIns().getPlayerData(p.getUniqueId());
            text = text.replace("%rankName%", PlayerPermissions.getIns().getGroup(p).getDisplay());
            text = text.replace("%online%", "" + String.valueOf(ServerManager.getIns().globalOnlinePlayers + Bukkit.getServer().getOnlinePlayers().size()));
            text = text.replace("%cash%", "" + pd.getCash());
            String line = s.get(i);
            if (line != null && line.equalsIgnoreCase(text)) {
                continue;
            }
            s.add(text, i);
        }
        s.update();
        p.setScoreboard(s.getScoreboard());
        board.put(p.getUniqueId(), s);
    }

    public void assignAllPlayerTeams(Player p, PlayerData pData) {
        SimpleScoreboard s = this.board.get(p.getUniqueId());
        Scoreboard scoreboard = s.getScoreboard();
        String groupName = PlayerPermissions.getIns().getGroup(p).getScoreboardName();
        List<PermissionsGroup> groups = PermissionsGroupManager.getIns().getServergroups();
        groups.forEach(group -> {
            Team team = scoreboard.getTeam(group.getScoreboardName());
            if (team == null) {
                team = scoreboard.registerNewTeam(group.getScoreboardName());
            }
            team.setPrefix(group.getPrefix());
            team.setSuffix(group.getSuffix());
        });
        for (Player pl : Bukkit.getOnlinePlayers()) {
            PlayerData d = PlayerManager.getIns().getPlayerData(pl.getUniqueId());
            PermissionsGroup group = PlayerPermissions.getIns().getGroup(pl);
            Team team = scoreboard.getTeam(group.getScoreboardName());
            team.addEntry(pl.getName());

            if (this.board.containsKey(pl.getUniqueId())) {
                SimpleScoreboard simpleScoreboard = this.board.get(pl.getUniqueId());
                Scoreboard scoreboard1 = simpleScoreboard.getScoreboard();
                scoreboard1.getTeam(groupName).addEntry(p.getName());
            }
        }
    }

    public void handlePlayerJoin(Player p, PlayerData d) {
        setupScoreboard(p, d);
        assignAllPlayerTeams(p, d);

        board.keySet().forEach(u -> {
            this.setupScoreboard(Bukkit.getPlayer(u), PlayerManager.getIns().getPlayerData(u));
        });
    }

    public void handlePlayerDC(Player p, PlayerData d) {
        this.board.remove(d.getId());
        String groupName = PlayerPermissions.getIns().getGroup(p).getScoreboardName();
        this.board.forEach((id, scoreboard) -> {
            scoreboard.getScoreboard().getTeam(groupName).removeEntry(p.getName());
        });

        board.keySet().forEach(u -> {
            this.setupScoreboard(Bukkit.getPlayer(u), PlayerManager.getIns().getPlayerData(u));
        });
    }

}
