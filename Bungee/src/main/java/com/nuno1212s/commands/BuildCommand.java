package com.nuno1212s.commands;

import com.nuno1212s.confighandler.Config;
import com.nuno1212s.loginhandling.SessionData;
import com.nuno1212s.loginhandling.SessionHandler;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BuildCommand extends Command {

    public BuildCommand() {
        super("build");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            SessionData session = SessionHandler.getIns().getSession(player.getUniqueId());
            if (session == null || !session.isAuthenticated()) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Config.getIns().getC().getString("Message.NotLoggedIn", "&cNão podes fazer este comando sem estar logado."))));
                return;
            }


            PlayerData pd = PlayerManager.getIns().getPlayer(sender.getName());
            if (!pd.hasPermission("novus.bungee.command.build")) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Config.getIns().getC().getString("Message.WithoutPermission", "&cWithout permission.."))));
                return;
            }

            String BuildServer = Config.getIns().getC().getString("BuildServer", "build");

            if (ProxyServer.getInstance().getServerInfo(BuildServer) != null) {
                ServerInfo target = ProxyServer.getInstance().getServerInfo(BuildServer);

                player.connect(target);


                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Config.getIns().getC().getString("Message.SendToBuildServer", "&aYou have been sent to Build Server."))));
            }

        } else {
            sender.sendMessage(TextComponent.fromLegacyText(Config.getIns().getC().getString("Message.OnlyPlayers", "Only players can run this command.")));
        }

    }
}
