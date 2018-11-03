package com.nuno1212s.party.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.party.PartyMain;
import com.nuno1212s.party.exceptions.PlayerHasNoPartyException;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.util.CommandUtil.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RemovePlayerFromPartyCommand implements Command<ProxiedPlayer> {
    @Override
    public String[] names() {
        return new String[]{"remove"};
    }

    @Override
    public String usage() {
        return "/party remove <player>";
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args) {

        if (args.length < 2) {

            player.sendMessage(TextComponent.fromLegacyText(usage()));

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

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[1]);

        if (proxiedPlayer == null || !proxiedPlayer.isConnected()) {

            MainData.getIns().getMessageManager().getMessage("NO_PLAYER_WITH_THAT_NAME")
                    .sendTo(player);

            return;
        }


        try {
            PartyMain.getIns().getPartyManager().removePlayerFromParty(proxiedPlayer.getUniqueId());
        } catch (PlayerHasNoPartyException e) {



        }

    }
}
