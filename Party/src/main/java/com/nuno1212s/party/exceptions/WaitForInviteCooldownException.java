package com.nuno1212s.party.exceptions;

public class WaitForInviteCooldownException extends Throwable {

    public WaitForInviteCooldownException() {
        super("Wait for the invite to cooldown", null, false, false);
    }

}
