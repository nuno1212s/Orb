package com.nuno1212s.party;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.party.commands.PartyCommand;
import com.nuno1212s.party.invites.InviteManager;
import com.nuno1212s.party.partymanager.PartyManager;
import com.nuno1212s.party.redishandling.RedisHandler;
import lombok.Getter;

@ModuleData(name = "Party", version = "0.1-SNAPSHOT")
public class PartyMain extends Module {

    @Getter
    static PartyMain ins;

    @Getter
    private PartyManager partyManager;

    @Getter
    private InviteManager inviteManager;

    @Getter
    private RedisHandler redis;

    @Override
    public void onEnable() {
        ins = this;

        partyManager = new PartyManager();
        inviteManager = new InviteManager();
        redis = new RedisHandler();

        registerCommand(new String[]{"party"}, new PartyCommand());
    }

    @Override
    public void onDisable() {

    }
}
