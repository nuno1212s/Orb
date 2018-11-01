package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class InvitePlayerCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"invite"};
    }

    @Override
    public String usage() {
        return "/party invite <player>";
    }

    @Override
    public void execute(Player player, String[] strings) {

        Party p = PartyMain.getIns().getPartyManager().getPartyForPlayer(player.getUniqueId());

        if (p == null) {

            MainData.getIns().getMessageManager().getMessage("NO_PARTY")
                    .sendTo(player);

            return;
        }

        if (strings.length < 1) {

            player.sendMessage(usage());

            return;
        }

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(strings[1]);

    }
}
