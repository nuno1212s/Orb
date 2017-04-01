package com.nuno1212s.commands;

import com.nuno1212s.confighandler.Config;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import com.nuno1212s.loginhandling.SessionData;
import com.nuno1212s.loginhandling.SessionHandler;

public class RCommand extends Command {

    public RCommand() {
        super("r");
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


            if (args.length > 0) {

                String v = PlayerManager.getIns().lastTell.get(player.getName());
                if (BungeeCord.getInstance().getPlayer(v) == null) {
                    player.sendMessage(new ComponentBuilder("Jogador não encontrado.").color(ChatColor.RED).create());
                    return;
                }

                ProxiedPlayer victim = BungeeCord.getInstance().getPlayer(v);
                if (victim.isConnected()) {

                    PlayerData vpd = PlayerManager.getIns().getPlayer(victim.getName());
                    if (!vpd.getTell()) {
                        player.sendMessage(new ComponentBuilder("Este jogador tem o tell desativado.").color(ChatColor.RED).create());
                        return;
                    }

                    String msg = "";
                    for (String s : args) {
                        msg = msg + s + " ";
                    }

                    TextComponent message = new TextComponent("[");
                    message.setColor(ChatColor.GRAY);
                    TextComponent p1 = new TextComponent(player.getName());
                    p1.setColor(ChatColor.AQUA);
                    message.addExtra(p1);
                    TextComponent a1 = new TextComponent(" -> ");
                    a1.setColor(ChatColor.GRAY);
                    message.addExtra(a1);
                    TextComponent p2 = new TextComponent(victim.getName());
                    p2.setColor(ChatColor.AQUA);
                    message.addExtra(p2);
                    TextComponent a2 = new TextComponent("] ");
                    a2.setColor(ChatColor.GRAY);
                    message.addExtra(a2);
                    TextComponent m = new TextComponent(msg);
                    m.setColor(ChatColor.WHITE);
                    message.addExtra(m);

                    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " "));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Responder a " + player.getName()).color(ChatColor.YELLOW).create()));
                    victim.sendMessage(message);

                    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + victim.getName() + " "));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Responder a " + victim.getName()).color(ChatColor.YELLOW).create()));
                    player.sendMessage(message);

                    PlayerManager.getIns().lastTell.put(victim.getName(), player.getName());
                    PlayerManager.getIns().lastTell.put(player.getName(), victim.getName());
                } else {

                    player.sendMessage(new ComponentBuilder("Jogador não encontrado.").color(ChatColor.RED).create());
                    return;

                }

            } else {

                sender.sendMessage(new ComponentBuilder("Uso incorreto. Use /r <mensagem>").color(ChatColor.YELLOW).create());

            }

        } else {
            sender.sendMessage(new ComponentBuilder("Este comando apenas pode ser executado por um jogador.")
                    .color(ChatColor.RED).create());
        }

    }

}
