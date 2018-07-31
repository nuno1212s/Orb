package com.nuno1212s.bungee.commands;

import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.mysql.MySQL;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ActivateAutoLogin extends Command {

    public ActivateAutoLogin() {
        super("ativarautologin", "autologin", "activateautologin");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) commandSender;
        if (SessionHandler.getIns().getSession(p.getUniqueId()) != null
                && SessionHandler.getIns().getSession(p.getUniqueId()).isAuthenticated()) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            if (playerData.isAutoLogin()) {
                commandSender.sendMessage(
                        TextComponent.fromLegacyText(
                                MainData.getIns().getMessageManager().getMessage("AUTOLOGIN_ALREADY_ACTIVE").toString()));

                return;
            }

            if (playerData.isPremium()) {

                playerData.setAutoLogin(true);

                MainData.getIns().getScheduler().runTaskAsync(() -> {
                    MySQL.updateAutoLogin(playerData);
                });

                commandSender.sendMessage(
                        TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("ACTIVATED_AUTOLOGIN").toString())
                );

            } else {

                commandSender.sendMessage(
                        TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("PREMIUM_REQUIRED_FOR_AUTOLOGIN").toString())
                );

            }
        }

    }
}
