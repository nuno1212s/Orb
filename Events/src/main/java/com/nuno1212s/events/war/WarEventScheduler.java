package com.nuno1212s.events.war;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.events.war.inventories.SelectPlayersInventory;
import com.nuno1212s.events.war.util.WarEventHelper;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WarEventScheduler {

    public static int MINIMUM_PLAYER_PER_CLAN = 5, MAX_PLAYERS_PER_CLAN = 10;

    private static List<Integer> minutesToAnnounce = Arrays.asList(30, 20, 15, 10, 5, 4, 3, 2, 1)
            , secondsToAnnounce = Arrays.asList(30, 15, 10, 5, 4, 3, 2, 1);

    private Map<String, List<UUID>> signedUpClans;

    public long startDate;

    private File dataFile;

    @Getter
    private SelectPlayersInventory selectPlayersInventory;

    @Getter
    private WarEventHelper helper;

    @Getter
    private WarEvent onGoing;

    private List<Integer> minutesAnnounced = new ArrayList<>(), secondsAnnounced = new ArrayList<>();

    public WarEventScheduler(Module m) {

        this.signedUpClans = new HashMap<>();

        File jsonFile = new File(m.getDataFolder(), "selectPlayersInventory.json");

        if (!jsonFile.exists()) {
            m.saveResource(jsonFile, "selectPlayersInventory.json");
        }

        this.selectPlayersInventory = new SelectPlayersInventory(jsonFile);

        dataFile = new File(m.getDataFolder(), "nextWarEvent.json");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.startDate = getNextSaturday();
        } else {

            try (Reader r = new FileReader(dataFile)) {
                JSONObject object = (JSONObject) new JSONParser().parse(r);

                this.startDate = (Long) object.get("StartDate");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }

        }

        this.helper = new WarEventHelper(m);
    }

    public void save() {

        this.helper.save();

        try (Writer r = new FileWriter(dataFile)) {

            JSONObject json = new JSONObject();

            json.put("StartDate", this.startDate);

            json.writeJSONString(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send the time for the
     */
    public void checkTime() {

        if (this.startDate < System.currentTimeMillis()) {
            start();
            return;
        }

        for (Integer time : minutesToAnnounce) {
            if (this.startDate - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(time)) {
                if (!this.minutesAnnounced.contains(time)) {

                    this.minutesAnnounced.add(time);

                    this.helper.sendMessage(this.getPlayersRegistered(),
                            MainData.getIns().getMessageManager().getMessage("WAR_EVENT_STARTS_IN")
                                    .format("%minutes%", time));

                }

            }
        }

        for (int time : secondsToAnnounce) {
            if (this.startDate - System.currentTimeMillis() <= TimeUnit.SECONDS.toMillis(time)) {
                if (!this.secondsAnnounced.contains(time)) {

                    this.secondsAnnounced.add(time);

                    this.helper.sendMessage(this.getPlayersRegistered(),
                            MainData.getIns().getMessageManager().getMessage("WAR_EVENT_STARTS_SECONDS")
                                    .format("%seconds%", time));

                }
            }
        }

    }

    /**
     * Only allow for clans to register in the 30 minutes before the war event starts
     *
     * @return
     */
    public boolean canRegisterClan() {
        return this.startDate - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Registers a given clan into the war event
     *
     * @param c
     */
    public void registerClan(Clan c, Player register) {

        if (!canRegisterClan()) {
            return;
        }

        List<UUID> onlineMembers = c.getOnlineMembers();
        if (onlineMembers.size() < MINIMUM_PLAYER_PER_CLAN) {
            MainData.getIns().getMessageManager().getMessage("NOT_ENOUGH_MEMBERS_ONLINE").sendTo(register);

        } else if (onlineMembers.size() > MAX_PLAYERS_PER_CLAN) {

            MainData.getIns().getMessageManager().getMessage("TOO_MANY_MEMBERS_ONLINE").sendTo(register);

            //Open inventory
            register.openInventory(this.selectPlayersInventory.buildInventory(register));
        } else {

            MainData.getIns().getMessageManager().getMessage("CLAN_REGISTERED").sendTo(register);

            registerClan(c.getClanID(), onlineMembers);

        }

    }

    public void registerClan(String clanID, List<UUID> membersAssigned) {

        if (!canRegisterClan()) {
            return;
        }

        this.signedUpClans.put(clanID, membersAssigned);

        for (UUID onlineMember : membersAssigned) {
            Player player = Bukkit.getPlayer(onlineMember);

            player.teleport(this.helper.getSpectatorLocation());
        }
    }

    public void registerPlayer(String clanID, UUID player) {

        if (!canRegisterClan()) {
            return;
        }

        if (!this.signedUpClans.containsKey(clanID)) {
            return;
        }

        List<UUID> memberSignedUp = this.signedUpClans.get(clanID);

        memberSignedUp.add(player);

        this.signedUpClans.put(clanID, memberSignedUp);

        Player p = Bukkit.getPlayer(player);

        if (p == null) {
            return;
        }

        this.helper.sendMessage(getPlayersRegistered(clanID),
                MainData.getIns().getMessageManager().getMessage("PLAYER_JOINED")
                        .format("%player%", p.getName()));

        p.teleport(this.helper.getSpectatorLocation());

    }

    /**
     * Get all the players registered in this war event
     *
     * @return
     */
    public List<UUID> getPlayersRegistered() {

        List<UUID> players = new ArrayList<>();

        this.signedUpClans.values().forEach(players::addAll);

        return players;
    }

    public List<UUID> getPlayersRegistered(String clanID) {
        return this.signedUpClans.getOrDefault(clanID, new ArrayList<>());
    }

    /**
     * Unregister a player from the war event
     *
     * @param playerID
     */
    public void removePlayer(UUID playerID) {

        for (Map.Entry<String, List<UUID>> players : this.signedUpClans.entrySet()) {

            if (players.getValue().contains(playerID)) {
                Clan c = ClanMain.getIns().getClanManager().getClan(players.getKey());

                if (c == null) {
                    break;
                }

                Player p = Bukkit.getPlayer(playerID);

                p.teleport(this.helper.getFallbackLocation());

                this.getHelper().sendMessage(this.getPlayersRegistered(c.getClanID()),
                        MainData.getIns().getMessageManager().getMessage("PLAYER_LEFT")
                                .format("%player%", p.getName()));

                players.getValue().remove(playerID);

                if (players.getValue().size() < MINIMUM_PLAYER_PER_CLAN) {
                    disqualify(c);

                    break;
                }

                List<UUID> onlineMembers = c.getOnlineMembers();


                //There are still some more online members
                if (onlineMembers.size() > players.getValue().size()) {

                    for (UUID onlineMember : onlineMembers) {
                        if (c.getRank(onlineMember).ordinal() >= Clan.Rank.ADMIN.ordinal()) {

                            Player player = Bukkit.getPlayer(onlineMember);

                            this.selectPlayersInventory.getSelected().put(player.getUniqueId(), players.getValue());

                            player.openInventory(this.selectPlayersInventory.buildInventory(player, c));

                            break;
                        }
                    }

                }
            }

        }

    }

    /**
     * Disqualifies a clan from the war event
     *
     * @param c
     */
    public void disqualify(Clan c) {

        this.signedUpClans.get(c.getClanID()).forEach((player) -> {

            Player p = Bukkit.getServer().getPlayer(player);

            p.teleport(this.helper.getFallbackLocation());

        });

        this.signedUpClans.remove(c.getClanID());

    }

    /**
     * Get the clans that are registered for the next war event
     *
     * @return
     */
    public Set<String> getRegisteredClans() {
        return this.signedUpClans.keySet();
    }

    /**
     * Is this clan registered in the war event
     *
     * @param clanID
     * @return
     */
    public boolean isClanRegistered(String clanID) {
        return this.signedUpClans.containsKey(clanID);
    }

    /**
     * Start the war event
     */
    public void start() {

        if (this.signedUpClans.size() < 2) {
            MainData.getIns().getMessageManager().getMessage("NOT_ENOUGH_CLANS_SIGNED_UP")
                    .sendTo(Bukkit.getServer().getOnlinePlayers());

            this.reset(this.startDate + TimeUnit.DAYS.toMillis(7));

            return;
        }

        this.onGoing = new WarEvent(this.signedUpClans, this.helper);

        getPlayersRegistered().forEach((player) -> {
            Player p = Bukkit.getPlayer(player);

            if (p == null || !p.isOnline()) {
                return;
            }

            p.teleport(this.helper.getRandomSpawnLocation());

        });

        this.reset(this.startDate + TimeUnit.DAYS.toMillis(7));
    }

    /**
     * Handles the end of a war event
     */
    public void handleEnd() {

        this.helper.addPrevious(this.onGoing);

        this.onGoing = null;

    }

    public void reset(long newStartDate) {
        this.signedUpClans = new HashMap<>();

        this.startDate = newStartDate;

        this.secondsAnnounced = new ArrayList<>();
        this.minutesAnnounced = new ArrayList<>();
    }

    private long getNextSaturday() {

        LocalDateTime date = LocalDateTime.now();

        date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        date = date.withHour(14).withMinute(0).withSecond(0);

        return date.toInstant(ZoneOffset.UTC).toEpochMilli();

    }

}
