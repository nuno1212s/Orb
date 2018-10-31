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

        Party party = new Party(owner);

        this.parties.add(party);

        return party;
    }

    public void destroyParty(UUID owner) {

        Party party = getPartyByOwner(owner);

        if (party == null) {
            return;
        }

        PartyMain.getIns().getInviteManager().handlePartyDestruction(party);
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

        Party partyForPlayer = getPartyForPlayer(player);

        if (partyForPlayer == null) {
            throw new PlayerHasNoPartyException();
        }

        partyForPlayer.removeMember(player);
    }

    public boolean addPlayerToParty(UUID player, Party p) {
        return p.addMember(player);
    }

}
