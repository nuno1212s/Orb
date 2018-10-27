package com.nuno1212s.party.partymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    public static final int MAX_PLAYERS = 4;

    private UUID owner;

    private List<UUID> members;

    Party(UUID owner) {

        this.owner = owner;

        this.members = new ArrayList<>(MAX_PLAYERS);

    }

    void addMember(UUID memberID) {
        this.members.add(memberID);
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

}
