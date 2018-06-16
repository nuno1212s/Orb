package com.nuno1212s.bungee.commands;

import com.nuno1212s.bungee.loginhandler.SessionData;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Handles staff chat
 */
public class StaffChatCommand extends Command {

    public StaffChatCommand() {
        super("sc");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        PlayerData data = MainData.getIns().getPlayerManager().getPlayer(((ProxiedPlayer) commandSender).getUniqueId());

        SessionData session = SessionHandler.getIns().getSession(((ProxiedPlayer) commandSender).getUniqueId());
        if (session == null || !session.isAuthenticated()) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cNão podes fazer este comando sem estar logado.")));
            return;
        }

        if (!data.getMainGroup().hasPermission("staff")) {
            ((ProxiedPlayer) commandSender).sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Não tens permissão para fazer isto"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            message.append(args[i]);
            message.append(" ");
        }

        BaseComponent[] baseComponents = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&d[SC] " + data.getNameWithPrefix() + " &f: " + message.toString()));

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            PlayerData receivingPlayer = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

            if (receivingPlayer.getMainGroup().hasPermission("staff")) {
                player.sendMessage(baseComponents);
            }

        }

    }
}
