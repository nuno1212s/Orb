package com.nuno1212s.party.partymanager;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;
import com.nuno1212s.party.timers.CheckOwnerOnline;
import com.nuno1212s.playermanager.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {

    private List<Party> parties;

    public PartyManager() {

        this.parties = new ArrayList<>();

        MainData.getIns().getScheduler().runTaskTimerAsync(new CheckOwnerOnline(), 1200, 3600);
    }

    public Party createNewParty(UUID owner) {
        return createNewParty(owner, true);
    }

    public Party createNewParty(UUID owner, boolean shouldUseRedis) {

        Party party = new Party(owner);

        this.parties.add(party);

        if (shouldUseRedis)
            PartyMain.getIns().getRedis().createParty(owner);

        return party;
    }

    public void destroyParty(UUID owner) {
        destroyParty(owner, true);
    }

    public void destroyParty(UUID owner, boolean shouldUseRedis) {

        Party party = getPartyByOwner(owner);

        if (party == null) {
            return;
        }

        PartyMain.getIns().getInviteManager().handlePartyDestruction(party);

        this.parties.remove(party);

        party.delete();

        if (shouldUseRedis) {
            PartyMain.getIns().getRedis().deleteParty(owner);
        }

    }

    public Party getPartyForPlayer(UUID playerID) {

        for (Party party : parties) {

            if (party.isMember(playerID)) {

                return party;
            }
        }

        return null;
    }

    public Party getPartyByOwner(UUID player) {

        for (Party party : parties) {
            if (party.isOwner(player)) {
                return party;
            }
        }

        return null;
    }

    public void removePlayerFromParty(UUID player) throws PlayerHasNoPartyException {
        removePlayerFromParty(player, true);
    }

    public void removePlayerFromParty(UUID player, boolean shouldUseRedis) throws PlayerHasNoPartyException {

        Party partyForPlayer = getPartyForPlayer(player);

        if (partyForPlayer == null) {
            throw new PlayerHasNoPartyException();
        }

        partyForPlayer.removeMember(player);

        PlayerData pD = MainData.getIns().getPlayerManager().getPlayer(player);

        if (pD != null && pD.isPlayerOnServer()) {

            MainData.getIns().getMessageManager().getMessage("LEFT_PARTY")
                    .sendTo(pD);

        }

        if (shouldUseRedis)
            PartyMain.getIns().getRedis().removePlayerFromParty(partyForPlayer.getOwner(), player);
    }

    public boolean addPlayerToParty(UUID player, Party p) {
        return p.addMember(player);
    }

    public List<Party> getParties() {

        return ImmutableList.copyOf(this.parties);

    }

}
