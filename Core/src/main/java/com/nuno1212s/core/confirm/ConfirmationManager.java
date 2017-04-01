package com.nuno1212s.core.confirm;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the confirmations
 */
public class ConfirmationManager {

    private static ConfirmationManager ins;

    public static ConfirmationManager getIns() {
        return ins;
    }

    private Set<Confirmation> confirmation;

    public ConfirmationManager() {
        ins = this;
        confirmation = new HashSet<>();
    }

    public Confirmation getConfirmation(UUID u) {
        for (Confirmation c : confirmation) {
            if (c.getId().equals(u)) {
                return c;
            }
        }
        return null;
    }

    public Confirmation getConfirmationByPlayer(UUID u) {
        for (Confirmation c : confirmation) {
            if (c.getPlayer().equals(u)) {
                return c;
            }
        }
        return null;
    }

    public void addConfirmation(Confirmation c) {
        if (getConfirmationByPlayer(c.getPlayer()) != null) {
            do {
                removeConfirmation(getConfirmationByPlayer(c.getPlayer()));
            } while (getConfirmationByPlayer(c.getPlayer()) != null);
        }
        this.confirmation.add(c);
    }

    public void removeConfirmation(Confirmation c) {
        this.confirmation.remove(c);
    }

}
