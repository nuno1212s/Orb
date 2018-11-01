package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class CreatePartyCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"create"};
    }

    @Override
    public String usage() {
        return "/party create";
    }

    @Override
    public void execute(Player player, String[] strings) {

        Party partyForPlayer = PartyMain.getIns().getPartyManager().getPartyForPlayer(player.getUniqueId());

        if (partyForPlayer == null) {

            MainData.getIns().getMessageManager().getMessage("ALREADY_HAVE_PARTY")
                    .sendTo(player);

            return;
        }

        PartyMain.getIns().getPartyManager().createNewParty(player.getUniqueId());
        MainData.getIns().getMessageManager().getMessage("CREATED_PARTY")
                .sendTo(player);
    }
}
