package com.nuno1212s.commands;

import com.nuno1212s.confighandler.Config;
import com.nuno1212s.permissions.PermissionsGroup;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import com.nuno1212s.loginhandling.SessionData;
import com.nuno1212s.loginhandling.SessionHandler;
import com.nuno1212s.permissions.PermissionsAPI;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;

public class SCommand extends Command {

    public SCommand() {
        super("s");
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
            if (!pd.hasPermission("novus.bungee.command.s")) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Config.getIns().getC().getString("Message.WithoutPermission", "&cWithout permission."))));
                return;
            }

            String msg = getMessage(args);

            synchronized (PlayerManager.getIns().getOnlinePlayers()) {
                for (PlayerData playerdata : PlayerManager.getIns().getOnlinePlayers()) {

                    PermissionsGroup group = PermissionsAPI.getIns().getGroup(playerdata.getGroupId());
                    if (group != null) {
                        if (group.hasPermission("novus.bungee.command.s")) {

                            String text = ChatColor.translateAlternateColorCodes('&', "&d&l(&dSTAFF&d&l) &f" + pd.getNameWithPrefix() + "&f: " + msg);

                            ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(playerdata.getName());
                            if (pp == null || !pp.isConnected()) {
                                continue;
                            }

                            pp.sendMessage(TextComponent.fromLegacyText(text));

                        }
                    }

                }
            }

        }

    }

    private String getMessage(String[] args) {
        String msg = "";
        for (String s : args) {
            msg = msg + s + " ";
        }
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }

}
