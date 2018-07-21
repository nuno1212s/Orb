package com.nuno1212s.clans.clanplayer;

import com.google.common.collect.ImmutableSet;

public interface ClanPlayer {

    /**
     * Get the clan the player is in
     *
     * Returns the clanID
      * @return
     */
    String getClan();

    void setClan(String clanID);

    boolean hasClan();

    int getKills();

    void setKills(int kills);

    int getDeaths();

    void setDeaths(int deaths);

    ImmutableSet<String> getInvites();

    boolean addInvite(String clanID);

    boolean removeInvite(String clanID);
}
