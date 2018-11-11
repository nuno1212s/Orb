package com.nuno1212s.party;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.party.commands.PartyCommand;
import com.nuno1212s.party.invites.InviteManager;
import com.nuno1212s.party.partymanager.PartyManager;
import com.nuno1212s.party.redishandling.RedisHandler;
import lombok.Getter;

import java.io.File;

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

        File f = new File(this.getDataFolder(), "messages.json");

        if (!f.exists()) {
            saveResource(f, "messages.json");
        }

        MainData.getIns().getMessageManager().addMessageFile(f);
    }

    @Override
    public void onDisable() {

    }
}
