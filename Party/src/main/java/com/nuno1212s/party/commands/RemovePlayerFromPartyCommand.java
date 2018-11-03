package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.entity.Player;

public class RemovePlayerFromPartyCommand implements Command<Player> {
    @Override
    public String[] names() {
        return new String[]{"remove"};
    }

    @Override
    public String usage() {
        return "/party remove <player>";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length < 2) {

            player.sendMessage(usage());

            return;
        }

        Party p = PartyMain.getIns().getPartyManager().getPartyForPlayer(player.getUniqueId());

        if (p == null) {

            MainData.getIns().getMessageManager().getMessage("NO_PARTY")
                    .sendTo(player);

            return;
        }

        if (!p.getOwner().equals(player.getUniqueId())) {

            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION")
                    .sendTo(player);

            return;
        }

        MainData.getIns().getPlayerManager().loadPlayer(args[1])
                .thenAccept((d) -> {

                    if (d == null || !d.isOnline()) {

                        MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_ONLINE")
                                .sendTo(player);

                        return;
                    }

                    if (d.getPlayerID().equals(player.getUniqueId())) {

                        MainData.getIns().getMessageManager().getMessage("CANNOT_KICK_YOURSELF")
                                .sendTo(player);

                        return;
                    }

                    Party playerParty = PartyMain.getIns().getPartyManager().getPartyForPlayer(d.getPlayerID());

                    if (playerParty.getOwner().equals(p.getOwner())) {

                        try {
                            PartyMain.getIns().getPartyManager().removePlayerFromParty(d.getPlayerID());
                        } catch (PlayerHasNoPartyException e) {
                            System.out.println("Failed");
                        }

                        MainData.getIns().getMessageManager().getMessage("PLAYER_KICKED")
                                .sendTo(player);

                    } else {
                        MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_IN_YOUR_PARTY")
                                .sendTo(player);
                    }

                });

    }
}
