package com.nuno1212s.party.commands;

import com.nuno1212s.util.CommandUtil.commandexecutors.CommandManager;

public class PartyCommand extends CommandManager {

    public PartyCommand() {
        super();

        addCommand(new CreatePartyCommand());
        addCommand(new InvitePlayerCommand());
        addCommand(new AcceptInviteCommand());
        addCommand(new LeavePartyCommand());
        addCommand(new RejectInviteCommand());
        addCommand(new RemovePlayerFromPartyCommand());
        addCommand(new DeletePartyCommand());

    }

}
