package com.nuno1212s.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleScoreboard {

    private static Map<String, OfflinePlayer> cache = new HashMap<>();

    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;

    public SimpleScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.<Team>newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<String>());
    }

    /**
     * Get the scoreboard's title
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the scoreboard's title
     * @param title
     */
    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if (obj != null)
            obj.setDisplayName(title);
    }

    /**
     * Add text to the scoreboard
     * @param text The text that will show up
     * @param score The score of the text
     */
    public void add(String text, Integer score) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        if (get(score) != null && get(score).equalsIgnoreCase(text)) {
            return;
        }

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    /**
     * Remove an entry from the scoreboard
	
     *
     * @param score The score of the entry
     * @param text The text of the player
     * @return
     */
    public boolean remove(Integer score, String text) {
        return remove(score, text, true);
    }

    public boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if(b)
            removed.add(score);

        return true;
    }

    public String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score) &&
                    !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    /**
     * Get the text at a score
     * @param score The score
     * @return
     */
    public String get(int score) {
        String scr = null;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score)) {
                scr = entry.getKey();
                break;
            }
        }
        return scr;
    }

    @SuppressWarnings("deprecation")
	private Map.Entry<Team, OfflinePlayer> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[pos];
        OfflinePlayer result;

        if (!cache.containsKey(color.toString()))
            cache.put(color.toString(), Bukkit.getOfflinePlayer(color.toString()));

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    private void applyText(Team team, String text, OfflinePlayer result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();

        team.setPrefix(iterator.next());

        if (!team.hasEntry(result.getName()))
            team.addEntry(result.getName());

        if (text.length() > 16) {
            boolean add = false;
            String prefixColor = ChatColor.getLastColors(team.getPrefix());

            if (team.getPrefix().endsWith("§")) {
                add = true;
                team.setPrefix(team.getPrefix().substring(0, team.getPrefix().length() - 1));
                prefixColor = null;
            }

            String suffix = iterator.next();

            if (prefixColor == null) {
                prefixColor = (add ? "§" : "");
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

    @SuppressWarnings("deprecation")
	public void update() {
        if (updated.isEmpty()) {
            return;
        }

        if (obj == null) {
            obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            obj.setDisplayName(title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (int remove : removed) {
            for (String s : scoreboard.getEntries()) {
                Score score = obj.getScore(s);

                if (score == null)
                    continue;

                if (score.getScore() != remove)
                    continue;

                scoreboard.resetScores(s);
            }
        }
        removed.clear();

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Team t = scoreboard.getTeam(ChatColor.values()[text.getValue()].toString());
            Map.Entry<Team, OfflinePlayer> team;

            if(!updated.contains(text.getKey())) {
                continue;
            }

            if(t != null) {
                String color = ChatColor.values()[text.getValue()].toString();

                if (!cache.containsKey(color)) {
                    cache.put(color, Bukkit.getOfflinePlayer(color));
                }

                team = new AbstractMap.SimpleEntry<>(t, cache.get(color));
                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            obj.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void reset() {
        teams.forEach(Team::unregister);
        teams.clear();
        scores.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
        for (Player p : players) {
            if (p == null) {
                continue;
            }

            p.setScoreboard(scoreboard);
        }
    }

}