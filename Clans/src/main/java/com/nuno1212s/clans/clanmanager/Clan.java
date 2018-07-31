package com.nuno1212s.clans.clanmanager;

import com.google.common.collect.ImmutableMap;
import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;

import java.util.*;

@AllArgsConstructor
public class Clan {

    private static int ID_LENGTH = 15;

    @Getter
    private String clanID;

    @Getter
    private UUID creator;

    @Getter
    private Map<UUID, Rank> members;

    @Getter
    private String clanName, clanTag, clanDescription;

    @Getter
    private String applicableServer;

    private int kills, deaths;

    public Clan(UUID creator, String clanName, String clanTag, String clanDescription) {
        this.clanID = RandomStringUtils.random(ID_LENGTH, true, true);
        this.creator = creator;
        this.clanName = clanName;
        this.clanTag = clanTag;
        this.clanDescription = clanDescription;
        this.applicableServer = MainData.getIns().getServerManager().getServerType();

        this.members = new HashMap<>();

    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementKills() {
        this.kills++;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public int getKDD() {
        return this.kills - this.deaths;
    }

    public void addMember(UUID player) {
        if (this.members.size() > 39) {
            return;
        }

        this.members.put(player, Rank.MEMBER);
    }

    public void removeMember(UUID player) {
        this.members.remove(player);
    }

    public String membersToString() {

        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (UUID member : members.keySet()) {
            if (first) {
                builder.append(member.toString());
                builder.append(":");
                builder.append(members.get(member).name());
                first = false;
            } else {
                builder.append(",");
                builder.append(member.toString());
                builder.append(":");
                builder.append(members.get(member).name());
            }
        }

        return builder.toString();
    }

    /**
     * Set the rank of a player in the clan
     *
     * @param player
     * @param rank
     */
    public void setClanRank(UUID player, Rank rank) {
        if (this.members.containsKey(player))
            this.members.put(player, rank);
    }

    public Map<UUID, Rank> getMembers() {
        return members;
    }

    /**
     * Get the members, sorted by rank (OWNER -> MEMBER)
     *
     * @return
     */
    public ImmutableMap<UUID, Rank> getMembersSorted() {

        List<Map.Entry<UUID, Rank>> entries = new ArrayList<>(this.members.entrySet());

        entries.add(new AbstractMap.SimpleEntry<UUID, Rank>(this.creator, Rank.OWNER));

        entries.sort(Map.Entry.comparingByValue());

        ImmutableMap.Builder<UUID, Rank> builder = ImmutableMap.<UUID, Rank>builder();

        entries.forEach(builder::put);

        return builder.build();
    }

    public Rank getRank(UUID player) {
        if (player == this.creator) {
            return Rank.OWNER;
        }

        if (!this.members.containsKey(player)) {
            return null;
        }

        return this.members.get(player);

    }

    public void delete() {

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Pair<PlayerData, Boolean> player = null;

            this.members.put(this.creator, Rank.OWNER);

            Iterator<UUID> iterator = this.members.keySet().iterator();

            do {
                player = MainData.getIns().getPlayerManager().getOrLoadPlayer(iterator.next());

                PlayerData p = player.getKey();

                if (player.getValue()) {
                    p = MainData.getIns().getPlayerManager().requestAditionalServerData(p);
                }

                if (p != null) {

                    if (p instanceof ClanPlayer) {
                        ((ClanPlayer) p).setClan(null);

                        if (player.getValue()) {
                            p.save((o) -> {
                            });
                        } else {
                            MainData.getIns().getEventCaller().callUpdateInformationEvent(p, PlayerInformationUpdateEvent.Reason.OTHER);
                        }
                    }

                }

            } while (iterator.hasNext());

        });

    }

    public enum Rank {

        OWNER,
        ADMIN,
        MOD,
        MEMBER

    }

}
