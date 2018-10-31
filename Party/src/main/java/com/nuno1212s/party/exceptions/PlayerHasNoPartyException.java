package com.nuno1212s.party.exceptions;

public class PlayerHasNoPartyException extends Throwable {

    public PlayerHasNoPartyException() {
        super("The player has no party", null, false, false);
    }

}
