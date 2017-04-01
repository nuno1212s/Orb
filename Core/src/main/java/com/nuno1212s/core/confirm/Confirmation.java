package com.nuno1212s.core.confirm;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.nuno1212s.core.util.Callback;

import java.util.UUID;

/**
 * Handles the confirmation of commands
 */
@AllArgsConstructor
@Data
public class Confirmation {

    UUID id, player;

    Callback accept, deny;

    public void handleConfirmation() {
        accept.callback();
        ConfirmationManager.getIns().removeConfirmation(this);
    }

    public void handleDeny() {
        deny.callback();
        ConfirmationManager.getIns().removeConfirmation(this);
    }

}
