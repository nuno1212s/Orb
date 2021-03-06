package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class RejectInviteCommand implements Command<Player> {


    @Override
    public String[] names() {
        return new String[]{"reject"};
    }

    @Override
    public String usage() {
        return "/party reject <owner>";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length < 2) {

            player.sendMessage(usage());

            return;
        }

        Party p = PartyMain.getIns().getPartyManager().getPartyForPlayer(player.getUniqueId());

        if (p != null) {

            MainData.getIns().getMessageManager().getMessage("ALREADY_HAVE_PARTY")
                    .sendTo(player);

            return;
        }

        if (!PartyMain.getIns().getInviteManager().hasInvites(player.getUniqueId())) {

            MainData.getIns().getMessageManager().getMessage("NO_INVITES")
                    .sendTo(player);

            return;
        }

        MainData.getIns().getPlayerManager().loadPlayer(args[1])
                .thenAccept((d) -> {

                    if (d == null || !d.isOnline()) {

                        MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_ONLINE")
                                .sendTo(d);

                        return;
                    }

                    Party party = PartyMain.getIns().getPartyManager().getPartyByOwner(d.getPlayerID());

                    if (party != null) {

                        PartyMain.getIns().getInviteManager().rejectInvite(player.getUniqueId(), d.getPlayerID());

                        MainData.getIns().getMessageManager().getMessage("REJECTED_INVITE")
                                .sendTo(player);

                    } else {

                        MainData.getIns().getMessageManager().getMessage("PLAYER_HAS_NO_PARTY")
                                .sendTo(player);

                    }

                });

    }
}
