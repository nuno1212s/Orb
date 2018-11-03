package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class LeavePartyCommand implements Command<Player> {

    @Override
    public String[] names() {
        return new String[]{"leave"};
    }

    @Override
    public String usage() {
        return "/party leave";
    }

    @Override
    public void execute(Player player, String[] args) {

        Party p = PartyMain.getIns().getPartyManager().getPartyForPlayer(player.getUniqueId());

        if (p == null) {

            MainData.getIns().getMessageManager().getMessage("NO_PARTY")
                    .sendTo(player);

            return;
        }

        try {
            PartyMain.getIns().getPartyManager().removePlayerFromParty(player.getUniqueId());
        } catch (PlayerHasNoPartyException e) { }

    }
}
