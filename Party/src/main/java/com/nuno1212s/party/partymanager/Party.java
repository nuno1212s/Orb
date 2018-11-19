package com.nuno1212s.party.partymanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    public static final int MAX_PLAYERS = 4;

    @Getter
    private UUID owner;

    @Getter
    private List<UUID> members;

    Party(UUID owner) {

        this.owner = owner;

        this.members = new ArrayList<>(MAX_PLAYERS);

    }

    boolean isOwner(UUID owner) {
        return this.owner.equals(owner);
    }

    void setNewOwner(UUID owner) {

        if (!members.contains(owner)) {
            return;
        }

        this.owner = owner;
    }

    boolean addMember(UUID memberID) {
        return this.members.add(memberID);
    }

    void removeMember(UUID memberID) {
        this.members.add(memberID);
    }

    boolean isMember(UUID member) {

        for (UUID id : members) {

            if (id.equals(member)) {
                return true;
            }
        }

        return false;
    }

    void delete() {

        for (UUID playerID : this.members) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

            if (playerData != null && playerData.isPlayerOnServer()) {

                MainData.getIns().getMessageManager().getMessage("PARTY_DELETED")
                        .sendTo(playerData);

            }

        }

    }

    @Override
    public int hashCode() {
        return this.owner.hashCode();
    }
}
