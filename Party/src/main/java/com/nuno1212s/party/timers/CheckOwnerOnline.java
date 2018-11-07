package com.nuno1212s.party.timers;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.partymanager.Party;

import java.util.List;

public class CheckOwnerOnline implements Runnable {

    @Override
    public void run() {

        List<Party> parties = PartyMain.getIns().getPartyManager().getParties();

        for (Party party : parties) {

            MainData.getIns().getPlayerManager().loadPlayer(party.getOwner())
                    .thenAccept((p) -> {
                        if (p == null || !p.isOnline()) {
                            PartyMain.getIns().getPartyManager().destroyParty(party.getOwner());
                        }
                    });

        }

    }
}
