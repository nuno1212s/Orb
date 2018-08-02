package com.nuno1212s.clans.clanmanager;

import com.google.common.collect.ImmutableMap;
import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Clan {

    private static int ID_LENGTH = 15;

    public static int MAX_CLAN_MEMBERS = 25;

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
        if (this.members.size() >= MAX_CLAN_MEMBERS) {
            return;
        }

        addMember(player, true);
    }

    public void addMember(UUID member, boolean useRedis) {
        this.members.put(member, Rank.MEMBER);

        //We load the player because we always want to show that someone joined
        MainData.getIns().getPlayerManager().loadPlayer(member).thenAccept((d) -> {
            if (d != null) {
                if (d.isPlayerOnServer()) {
                    MainData.getIns().getEventCaller().callUpdateInformationEvent(d, PlayerInformationUpdateEvent.Reason.OTHER);
                }

                Message player_joined_clan = MainData.getIns().getMessageManager().getMessage("PLAYER_JOINED_CLAN")
                        .format("%playerName%", d.getPlayerName());

                for (UUID uuid : getOnlineMembers()) {

                    player_joined_clan.sendTo(MainData.getIns().getPlayerManager().getPlayer(uuid));

                }
            }
        });

        if (useRedis) {
            MainData.getIns().getScheduler().runTaskAsync(() -> {
                ClanMain.getIns().getRedisHandler().addMember(this, member);
            });
        }

    }

    public List<UUID> getOnlineMembers() {

        Set<UUID> uuids = new HashSet<>(this.members.keySet());

        uuids.add(this.creator);

        return uuids.stream().filter((uuid) -> {

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(uuid);

            return player != null && player.isPlayerOnServer();

        }).collect(Collectors.toList());

    }

    /**
     * Remove a member from a clan
     *
     * @param player The player to remove from the clan
     */
    public void removeMember(UUID player) {
        removeMember(player, true);
    }

    public void removeMember(UUID player, boolean useRedis) {
        this.members.remove(player);

        //We load the player because we always want to show that someone left
        MainData.getIns().getPlayerManager().loadPlayer(player).thenAccept((d) -> {
            if (d != null) {
                if (d.isPlayerOnServer())
                    MainData.getIns().getEventCaller().callUpdateInformationEvent(d, PlayerInformationUpdateEvent.Reason.OTHER);

                Message player_joined_clan = MainData.getIns().getMessageManager().getMessage("PLAYER_LEFT_CLAN")
                        .format("%playerName%", d.getPlayerName());

                for (UUID uuid : getOnlineMembers()) {

                    player_joined_clan.sendTo(MainData.getIns().getPlayerManager().getPlayer(uuid));

                }
            }
        });

        if (useRedis) {
            MainData.getIns().getScheduler().runTaskAsync(() ->
                    ClanMain.getIns().getRedisHandler().removeMember(this, player)
            );
        }
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
        setClanRank(player, rank, true);
    }

    public void setClanRank(UUID player, Rank rank, boolean useRedis) {
        if (this.members.containsKey(player)) {
            this.members.put(player, rank);

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(player);

            if (d != null && d.isPlayerOnServer()) {
                MainData.getIns().getMessageManager().getMessage("CLAN_RANK_CHANGED")
                        .format("%newRank%", rank.getName())
                        .sendTo(d);
            }

            if (useRedis) {
                ClanMain.getIns().getRedisHandler().setRank(this, player, rank);
            }
        }
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

        Collections.reverse(entries);

        ImmutableMap.Builder<UUID, Rank> builder = ImmutableMap.<UUID, Rank>builder();

        entries.forEach(builder::put);

        return builder.build();
    }

    public Rank getRank(UUID player) {
        if (player.equals(this.creator)) {
            return Rank.OWNER;
        }

        if (!this.members.containsKey(player)) {
            return null;
        }

        return this.members.get(player);

    }

    public void delete(boolean useDatabase) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            PlayerData player;

            this.members.put(this.creator, Rank.OWNER);

            Iterator<UUID> iterator = this.members.keySet().iterator();

            do {
                UUID playerID = iterator.next();

                if (useDatabase) {
                    Pair<PlayerData, Boolean> p = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerID);

                    if (p.getValue()) {
                        player = MainData.getIns().getPlayerManager().requestAditionalServerData(p.getKey());
                    } else {
                        player = p.getKey();
                    }

                } else {
                    player = MainData.getIns().getPlayerManager().getPlayer(playerID);

                    if (player == null)
                        continue;
                }

                if (player != null) {

                    if (player instanceof ClanPlayer) {
                        ((ClanPlayer) player).setClan(null);

                        if (!player.isPlayerOnServer()) {
                            player.save((o) -> {
                            });
                        } else {
                            MainData.getIns().getMessageManager().getMessage("CLAN_DISBANDED")
                                    .sendTo(player);

                            MainData.getIns().getEventCaller().callUpdateInformationEvent(player, PlayerInformationUpdateEvent.Reason.OTHER);
                        }
                    }

                }

            } while (iterator.hasNext());

        });
    }

    public void delete() {
        delete(true);
    }

    public enum Rank {

        MEMBER,
        MOD,
        ADMIN,
        OWNER;

        public String getName() {

            return MainData.getIns().getMessageManager().getMessage(name()).toString();

        }

    }

}
