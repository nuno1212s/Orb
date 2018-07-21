package com.nuno1212s.clans.clanmanager;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
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

        PlayerData d = MainData.getIns().getPlayerManager().getPlayer(creator.getUniqueId());

        Clan c = new Clan(creator.getUniqueId(), name, tag, desc);

        if (d instanceof ClanPlayer) {

            ((ClanPlayer) d).setClan(c.getClanID());

        }

        clans.put(c.getClanID(), c);

        MainData.getIns().getScheduler().runTaskAsync(() -> {

            ClanMain.getIns().getMySQLHandler().createClan(c);

        });

    }

    public void createClan(Player creator) {

        ClanMain.getIns().getChatRequests().createClan(creator);

    }

    public Clan getClan(String clanID) {
        return clans.get(clanID);
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
        });
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {

        clan.delete();

        this.clans.remove(clan.getClanID());

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            ClanMain.getIns().getMySQLHandler().removeClan(clan.getClanID());
        });
    }

    /**
     *
     * @param playerData
     */
    public void sendClanRanking(PlayerData playerData) {

        ArrayList<Clan> values = new ArrayList<>(this.clans.values());

        values.sort(Comparator.comparingInt(Clan::getKDD));

        Collections.reverse(values);

        Message clan_ranking = MainData.getIns().getMessageManager().getMessage("CLAN_RANKING");

        for (int i = 1; i <= 10 && i < values.size(); i++) {
            clan_ranking.format("%clanRank_" + String.valueOf(i) + "%", values.get(i).getClanName())
                    .format("%clanPoint_" + String.valueOf(i) + "%", values.get(i).getKDD());
        }

        clan_ranking.sendTo(playerData);
    }

}
