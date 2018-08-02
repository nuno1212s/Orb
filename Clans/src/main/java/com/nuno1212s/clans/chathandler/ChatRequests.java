package com.nuno1212s.clans.chathandler;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatRequests implements Listener {

    private Map<UUID, Callback<String>> players;

    public ChatRequests() {
        players = new HashMap<>();
    }

    public void requestChatInformation(Player player, String message, Callback<String> callback) {

        players.put(player.getUniqueId(), callback);

        MainData.getIns().getMessageManager().getMessage(message).sendTo(player);

    }

    public void createClan(Player creator) {

        requestChatInformation(creator, "CHOOSE_NAME", (name) -> {

            if (name.length() > 20 || name.length() < 5) {
                MainData.getIns().getMessageManager().getMessage("NAME_TOO_BIG").sendTo(creator);

                createClan(creator);

                return;
            }


            if (name.equalsIgnoreCase("cancelar")) {

                MainData.getIns().getMessageManager().getMessage("CLAN_CREATION_CANCELED").sendTo(creator);

                return;
            }

            Clan clanByName = ClanMain.getIns().getClanManager().getClanByName(name);

            if (clanByName != null) {
                MainData.getIns().getMessageManager().getMessage("CLAN_WITH_NAME_ALREADY_EXISTS")
                        .sendTo(creator);

                createClan(creator);

                return;
            }

            getTag(creator, name);
        });

    }

    private void getTag(Player creator, String name) {
        requestChatInformation(creator, "CHOOSE_TAG", (tag) -> {

            if (tag.length() != 3) {

                MainData.getIns().getMessageManager().getMessage("TAG_TOO_BIG").sendTo(creator);

                getTag(creator, name);

                return;
            }

            if (name.equalsIgnoreCase("cancelar")) {

                MainData.getIns().getMessageManager().getMessage("CLAN_CREATION_CANCELED").sendTo(creator);

                return;
            }

            Clan clanByName = ClanMain.getIns().getClanManager().getClanByTag(name);

            if (clanByName != null) {
                MainData.getIns().getMessageManager().getMessage("CLAN_WITH_TAG_ALREADY_EXISTS")
                        .sendTo(creator);

                getTag(creator, name);

                return;
            }

            getDescription(creator, name, tag);
        });
    }

    private void getDescription(Player creator, String name, String tag) {

        requestChatInformation(creator, "CHOOSE_DESCRIPTIONS", (desc) -> {

            if (desc.length() >= 250) {

                MainData.getIns().getMessageManager().getMessage("DESCRIPTION_TOO_BIG").sendTo(creator);

                getDescription(creator, name, tag);

                return;
            }

            if (name.equalsIgnoreCase("cancelar")) {

                MainData.getIns().getMessageManager().getMessage("CLAN_CREATION_CANCELED").sendTo(creator);

                return;
            }

            ClanMain.getIns().getClanManager().createClan(creator, name, tag, desc);
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (players.containsKey(e.getPlayer().getUniqueId())) {

            Callback<String> stringCallback = players.get(e.getPlayer().getUniqueId());

            players.remove(e.getPlayer().getUniqueId());

            stringCallback.callback(e.getMessage());

            e.setCancelled(true);


        }
    }

}
