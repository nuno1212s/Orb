package com.nuno1212s.party.invites;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.WaitForInviteCooldownException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.playermanager.PlayerData;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InviteManager {

    private static final long INVITE_EXPIRATION = TimeUnit.MINUTES.toMillis(3);

    private Map<Party, List<Invite>> invites;

    public InviteManager() {

        invites = new HashMap<>();

    }

    public void handlePartyDestruction(Party p) {
        invites.remove(p);
    }

    public boolean hasInvites(UUID player) {

        for (List<Invite> value : invites.values()) {

            for (Invite invite : value) {

                if (invite.getInvited().equals(player)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void createInvite(Party p, UUID player) throws WaitForInviteCooldownException {
        createInvite(p, player, true);
    }

    public void createInvite(Party p, UUID player, boolean shouldUseRedis) throws WaitForInviteCooldownException {

        List<Invite> invites = this.invites.get(p);

        if (invites == null) {
            invites = new ArrayList<>();
        } else {

            Invite invite = getInvite(p, player);

            if (invite != null) {

                if (System.currentTimeMillis() - invite.getTimeOfInvite() < INVITE_EXPIRATION) {

                    throw new WaitForInviteCooldownException();

                } else {

                    this.invites.get(p).remove(invite);

                }
            }
        }

        Invite inv = new Invite(player, System.currentTimeMillis());

        invites.add(inv);

        this.invites.put(p, invites);

        PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player);

        if (player1 != null && player1.isPlayerOnServer()) {

            MainData.getIns().getPlayerManager().loadPlayer(p.getOwner()).thenAccept((ownerData) ->
                    MainData.getIns().getMessageManager().getMessage("RECEIVED_INVITE")
                            .format("%owner%", ownerData.getNameWithPrefix())
                            .sendTo(player1));

        }

        if (shouldUseRedis) {
            PartyMain.getIns().getRedis().sendPartyInvite(p.getOwner(), player);
        }

    }

    public boolean acceptInvite(UUID playerID, UUID ownerID) {

        return acceptInvite(playerID, ownerID, true);

    }


    public boolean acceptInvite(UUID playerID, UUID inviteID, boolean shouldUseRedis) {

        Party partyByOwner = PartyMain.getIns().getPartyManager().getPartyByOwner(inviteID);

        if (partyByOwner == null) {

            return false;

        }

        if (shouldUseRedis) {

            PartyMain.getIns().getRedis().acceptPartyInvite(inviteID, playerID);

        }

        return PartyMain.getIns().getPartyManager().addPlayerToParty(playerID, partyByOwner);
    }

    /**
     * Get the invite
     *
     * @param p        The party that has invited the player
     * @param playerID
     * @return
     */
    public Invite getInvite(Party p, UUID playerID) {

        for (Invite invite : this.invites.get(p)) {
            if (invite.getInvited().equals(playerID)) {

                return invite;

            }
        }

        return null;
    }

    public void rejectInvite(UUID playerID, UUID partyOwnerID) {

        rejectInvite(playerID, partyOwnerID, true);
    }

    public void rejectInvite(UUID playerID, UUID partyOwnerID, boolean shouldUseRedis) {

        Party party = PartyMain.getIns().getPartyManager().getPartyByOwner(partyOwnerID);

        if (party == null) {
            return;
        }

        Invite invite = getInvite(party, playerID);

        this.invites.get(party).remove(invite);

        if (shouldUseRedis) {
            PartyMain.getIns().getRedis().revokePartyInvite(partyOwnerID, playerID);
        }
    }
}
