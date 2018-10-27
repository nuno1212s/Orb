package com.nuno1212s.party.partymanager;

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

        Party party = new Party(owner);

        this.parties.add(party);

        return party;
    }

    public Party getPartyForPlayer(UUID playerID) {

        for (Party party : parties) {

            if (party.isMember(playerID)) {

                return party;
            }
        }

        return null;
    }

    public void removePlayerFromParty(UUID player) throws PlayerHasNoPartyException {

        Party partyForPlayer = getPartyForPlayer(player);

        if (partyForPlayer == null) {
            throw new PlayerHasNoPartyException();
        }



    }

}
