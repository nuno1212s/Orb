package com.nuno1212s.core.commandmanager;

import com.nuno1212s.core.confirm.Confirmation;
import com.nuno1212s.core.confirm.ConfirmationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Handles the /deny command
 */
public class DenyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            return true;
        }
        try {
            UUID u = UUID.fromString(strings[0]);
            Confirmation confirmation = ConfirmationManager.getIns().getConfirmation(u);
            if (confirmation != null) {
                ConfirmationManager.getIns().removeConfirmation(confirmation);
                confirmation.handleDeny();
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }
}
