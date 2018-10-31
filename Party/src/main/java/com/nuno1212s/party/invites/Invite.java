package com.nuno1212s.party.invites;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Invite {

    UUID invited;

    long timeOfInvite;

}
