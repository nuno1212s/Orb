package com.nuno1212s.clans.redishandler;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.main.MainData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;

import java.util.UUID;

public class ClanRedisHandler implements RedisReceiver {

    @Override
    public String channel() {
        return "Clan";
    }


    @Override
    public void onReceived(Message message) {
        if (!message.getChannel().equalsIgnoreCase(channel())) {
            return;
        }

        if (message.getReason().equalsIgnoreCase("CLAN_CREATED")) {

            if ((MainData.getIns().getServerManager().isApplicable((String) message.getData().get("APPLICABLESERVER")))) {

                ClanMain.getIns().getClanManager().getOrLoadClan((String) message.getData().get("CLAN_ID"));

            }

        } else if (message.getReason().equalsIgnoreCase("DELETE_CLAN")) {

            Clan c = ClanMain.getIns().getClanManager().getClan((String) message.getData().get("CLAN_ID"));

            if (c == null) {
                return;
            }

            ClanMain.getIns().getClanManager().deleteClan(c, false);

        } else if (message.getReason().equalsIgnoreCase("ADD_MEMBER")) {

            Clan c = ClanMain.getIns().getClanManager().getClan((String) message.getData().get("CLAN_ID"));

            if (c != null) {
                c.addMember(UUID.fromString((String) message.getData().get("MEMBER")), false);
            }

        } else if (message.getReason().equalsIgnoreCase("REMOVE_MEMBER")) {

            Clan c = ClanMain.getIns().getClanManager().getClan((String) message.getData().get("CLAN_ID"));

            if (c != null) {
                c.removeMember(UUID.fromString((String) message.getData().get("MEMBER")), false);
            }

        } else if (message.getReason().equalsIgnoreCase("CHANGE_RANK")) {

            Clan c = ClanMain.getIns().getClanManager().getClan((String) message.getData().get("CLAN_ID"));

            if (c != null) {
                c.setClanRank(UUID.fromString((String) message.getData().get("MEMBER")),
                        Clan.Rank.valueOf((String) message.getData().get("RANK")),
                        false);
            }

        }

    }

    public void setRank(Clan c, UUID member, Clan.Rank r) {

        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("CHANGE_RANK")
                        .add("CLAN_ID", c.getClanID())
                        .add("MEMBER", member.toString())
                        .add("RANK", r.name())
                        .toByteArray());
    }

    /**
     * Broadcast an adition of a member to a clan
     *
     * @param c
     * @param member
     */
    public void addMember(Clan c, UUID member) {
        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("ADD_MEMBER")
                        .add("CLAN_ID", c.getClanID())
                        .add("MEMBER", member.toString())
                        .toByteArray());
    }


    /**
     * Broadcast remove member from clan
     *
     * @param c
     * @param player
     */
    public void removeMember(Clan c, UUID player) {

        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("REMOVE_MEMBER")
                        .add("CLAN_ID", c.getClanID())
                        .add("MEMBER", player.toString())
                        .toByteArray());
    }

    /**
     * Broadcast create a clan
     *
     * @param c
     */
    public void createClan(Clan c) {
        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("CLAN_CREATED")
                        .add("CLAN_ID", c.getClanID())
                        .add("APPLICABLESERVER", c.getApplicableServer())
                        .toByteArray());
    }

    /**
     * Broadcast delete a clan in all servers
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {

        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("DELETE_CLAN")
                        .add("CLAN_ID", clan.getClanID())
                        .toByteArray());

    }

}
