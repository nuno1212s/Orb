package com.nuno1212s.party.partymanager;

import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {

    private List<Party> parties;

    public PartyManager() {

        this.parties = new ArrayList<>();

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

        if (shouldUseRedis) {
            PartyMain.getIns().getRedis().deleteParty(owner);
        }

        this.parties.remove(party);

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

        if (shouldUseRedis)
            PartyMain.getIns().getRedis().removePlayerFromParty(partyForPlayer.getOwner(), player);
    }

    public boolean addPlayerToParty(UUID player, Party p) {
        return p.addMember(player);
    }

}
