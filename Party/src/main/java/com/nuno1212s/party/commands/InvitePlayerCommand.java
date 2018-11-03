package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.WaitForInviteCooldownException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class InvitePlayerCommand implements Command<Player> {

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

        if (strings.length < 1) {

            player.sendMessage(usage());

            return;
        }

        MainData.getIns().getPlayerManager().loadPlayer(strings[1])
                .thenAccept((d) -> {

                    if (d == null || !d.isOnline()) {
                        MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_OFFLINE")
                                .sendTo(player);

                        return;
                    }

                    Party party = p;

                    if (party == null) {
                        party = PartyMain.getIns().getPartyManager().createNewParty(player.getUniqueId());
                    }

                    try {
                        PartyMain.getIns().getInviteManager().createInvite(party, d.getPlayerID());
                    } catch (WaitForInviteCooldownException e) {

                        MainData.getIns().getMessageManager().getMessage("INVITE_COOLDOWN")
                                .sendTo(player);

                    }
                });
    }
}
