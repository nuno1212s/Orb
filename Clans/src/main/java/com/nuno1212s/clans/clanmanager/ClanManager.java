package com.nuno1212s.clans.clanmanager;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClanManager {

    private Map<String, Clan> clans;

    public ClanManager() {

        clans = new ConcurrentHashMap<>();

        loadClans();
    }

    public void loadClans() {

        List<Clan> clansForServer = ClanMain.getIns().getMySQLHandler().getClansForServer(MainData.getIns().getServerManager().getServerType());

        clansForServer.forEach(clan -> {
            clans.put(clan.getClanID(), clan);
        });

    }

    public void createClan(Player creator, String name, String tag, String desc) {

        createClan(creator, name, tag, desc, true);

    }

    public void createClan(Player creator, String name, String tag, String desc, boolean useDatabase) {
        PlayerData d = MainData.getIns().getPlayerManager().getPlayer(creator.getUniqueId());

        Clan c = new Clan(creator.getUniqueId(), name, tag, desc);

        if (d instanceof ClanPlayer) {

            ((ClanPlayer) d).setClan(c.getClanID());

        }

        clans.put(c.getClanID(), c);

        MainData.getIns().getEventCaller().callUpdateInformationEvent(d, PlayerInformationUpdateEvent.Reason.OTHER);

        MainData.getIns().getMessageManager().getMessage("CREATED_CLAN")
                .format("%clanName%", c.getClanName())
                .format("%clanTag%", c.getClanTag())
                .sendTo(creator);

        if (useDatabase) {
            MainData.getIns().getScheduler().runTaskAsync(() -> {
                ClanMain.getIns().getMySQLHandler().createClan(c);

                ClanMain.getIns().getRedisHandler().createClan(c);
            });
        }

    }

    public void createClan(Player creator) {

        ClanMain.getIns().getChatRequests().createClan(creator);

    }

    /**
     * Get clan by ID
     *
     * @param clanID
     * @return
     */
    public Clan getClan(String clanID) {
        return clans.get(clanID);
    }

    /**
     * Get clan by name
     *
     * @param clanName
     * @return
     */
    public Clan getClanByName(String clanName) {

        for (Clan clan : clans.values()) {
            if (clan.getClanName().equalsIgnoreCase(clanName)) {
                return clan;
            }
        }

        return null;
    }

    public Clan getClanByTag(String clanTag) {

        for (Clan clan : clans.values()) {
            if (clan.getClanTag().equalsIgnoreCase(clanTag)) {
                return clan;
            }
        }

        return null;
    }

    /**
     * Get or load a clan with a given ID
     *
     * @param clanID
     * @return
     */
    public CompletableFuture<Clan> getOrLoadClan(String clanID) {

        if (clans.containsKey(clanID)) {
            return CompletableFuture.completedFuture(clans.get(clanID));
        }

        return CompletableFuture.supplyAsync(() -> {
            return ClanMain.getIns().getMySQLHandler().getClanByID(clanID);
        }, MainData.getIns().getAsyncExecutor());
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {
        deleteClan(clan, true);
    }

    public void deleteClan(Clan clan, boolean useDatabase) {
        this.clans.remove(clan.getClanID());

        clan.delete(useDatabase);

        if (useDatabase) {
            MainData.getIns().getScheduler().runTaskAsync(() -> {
                ClanMain.getIns().getMySQLHandler().removeClan(clan.getClanID());

                ClanMain.getIns().getRedisHandler().deleteClan(clan);
            });
        }

    }

    public void save() {

        for (Clan clan : this.clans.values()) {
            ClanMain.getIns().getMySQLHandler().createClan(clan);
        }

    }

    /**
     * @param playerData
     */
    public void sendClanRanking(PlayerData playerData) {

        ArrayList<Clan> values = new ArrayList<>(this.clans.values());

        values.sort(Comparator.comparingInt(Clan::getKDD));

        System.out.println(values.size());

        Collections.reverse(values);

        Message clan_ranking = MainData.getIns().getMessageManager().getMessage("CLAN_RANKING");

        for (int i = 1; i <= 10 && i <= values.size(); i++) {
            clan_ranking.format("%clanRank_" + i + "%", values.get(i - 1).getClanName())
                    .format("%clanPoint_" + i + "%", values.get(i - 1).getKDD());
        }

        clan_ranking.sendTo(playerData);
    }

}
