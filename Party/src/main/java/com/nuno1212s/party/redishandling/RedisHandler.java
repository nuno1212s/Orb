package com.nuno1212s.party.redishandling;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;
import com.nuno1212s.party.exceptions.WaitForInviteCooldownException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.party.partymanager.PartyManager;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;

import java.util.UUID;

public class RedisHandler implements RedisReceiver {

    @Override
    public String channel() {
        return "PARTY";
    }

    @Override
    public void onReceived(Message message) {

        if (!message.getChannel().equalsIgnoreCase(channel())) {
            return;
        }

        if (message.getReason().equalsIgnoreCase("PARTY_CREATE")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER"));

            PartyMain.getIns().getPartyManager().createNewParty(ownerID, false);

        } else if (message.getReason().equalsIgnoreCase("PARTY_INVITE")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER")),
                    playerID = UUID.fromString((String) message.getData().get("ID"));
            Party party = PartyMain.getIns().getPartyManager().getPartyByOwner(ownerID);

            if (party == null) {
                return;
            }

            try {
                PartyMain.getIns().getInviteManager().createInvite(party, playerID, false);
            } catch (WaitForInviteCooldownException e) {

                System.out.println("This shouldn't happen ?");
                e.printStackTrace();

            }

        } else if (message.getReason().equalsIgnoreCase("DENY_INVITE")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER")),
                    playerID = UUID.fromString((String) message.getData().get("ID"));

            Party p = PartyMain.getIns().getPartyManager().getPartyByOwner(ownerID);

            if (p == null) {
                return;
            }

            PartyMain.getIns().getInviteManager().rejectInvite(playerID, ownerID, false);

        } else if (message.getReason().equalsIgnoreCase("ACCEPT_INVITE")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER")),
                    playerID = UUID.fromString((String) message.getData().get("ID"));

            Party p = PartyMain.getIns().getPartyManager().getPartyByOwner(ownerID);

            if (p == null) {
                return;
            }

            PartyMain.getIns().getInviteManager().acceptInvite(playerID, ownerID, false);

        } else if (message.getReason().equalsIgnoreCase("DELETE_PARTY")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER"));

            PartyMain.getIns().getPartyManager().destroyParty(ownerID, false);

        } else if (message.getReason().equalsIgnoreCase("REMOVE_PLAYER")) {

            UUID ownerID = UUID.fromString((String) message.getData().get("OWNER")),
                    playerID = UUID.fromString((String) message.getData().get("ID"));

            Party p = PartyMain.getIns().getPartyManager().getPartyByOwner(ownerID);

            if (p == null) {
                return;
            }

            try {
                PartyMain.getIns().getPartyManager().removePlayerFromParty(playerID, false);
            } catch (PlayerHasNoPartyException e) {
                System.out.println("The player has no party");
            }

        }

    }

    public void createParty(UUID partyOwner) {

        Message message = new Message("PARTY_CREATE");

        message.add("OWNER", partyOwner.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    public void sendPartyInvite(UUID partyOwner, UUID id) {

        Message message = new Message("PARTY_INVITE");

        message.add("OWNER", partyOwner.toString());
        message.add("ID", id.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    public void revokePartyInvite(UUID partyOwner, UUID playerID) {

        Message message = new Message("DENY_INVITE");

        message.add("OWNER", partyOwner.toString());
        message.add("ID", playerID.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    public void acceptPartyInvite(UUID partyOwner, UUID playerID) {

        Message message = new Message("ACCEPT_INVITE");

        message.add("OWNER", partyOwner.toString());
        message.add("ID", playerID.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    public void removePlayerFromParty(UUID partyOwner, UUID playerID) {

        Message message = new Message("REMOVE_PLAYER");

        message.add("OWNER", partyOwner.toString());
        message.add("ID", playerID.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    public void deleteParty(UUID partyOwner) {

        Message message = new Message("DELETE_PARTY");

        message.add("OWNER", partyOwner.toString());

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());

    }
}
