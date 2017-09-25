package com.nuno1212s.bungee.commands;

import com.google.common.collect.ImmutableSet;
import com.nuno1212s.bungee.loginhandler.SessionData;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.playermanager.BungeePlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class TellCommand extends Command implements TabExecutor {

    public TellCommand() {
        super("tell", "", "msg", "message");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            SessionData session = SessionHandler.getIns().getSession(player.getUniqueId());
            if (session == null || !session.isAuthenticated()) {
                sender.sendMessage(TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&', "&cNão podes fazer este comando sem estar logado.")));

                return;
            }

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

            Punishment punishment = d.getPunishment();
            if (punishment != null
                    && punishment.getPunishmentType() == Punishment.PunishmentType.MUTE
                    && !punishment.hasExpired()) {
                sender.sendMessage(TextComponent.fromLegacyText(
                        MainData.getIns().getMessageManager().getMessage("MUTED")
                                .format("%time%", punishment.timeToString())
                                .format("%reason%", punishment.getReason()).toString()));
                return;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    d.setTell(true);
                    player.sendMessage(TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("ACTIVATED_TELL").toString()));
                    return;
                }
                if (args[0].equalsIgnoreCase("off")) {
                    d.setTell(false);
                    player.sendMessage(TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("DEACTIVATED_TELL").toString()));
                    return;
                }
            }

            if (args.length > 1) {

                ProxiedPlayer victim = ProxyServer.getInstance().getPlayer(args[0]);

                if (victim != null && victim.isConnected()) {

                    if (victim.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Não podes enviar um tell a ti próprio."));
                        return;
                    }

                    PlayerData victimPlayerData = MainData.getIns().getPlayerManager().getPlayer(victim.getName());
                    if (!victimPlayerData.isTell() && !d.getMainGroup().hasPermission("staff")) {
                        player.sendMessage(TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("PLAYER_TELL_DEACTIVATED").toString()));
                        return;
                    }

                    if (!d.isTell() && !d.getMainGroup().hasPermission("staff")) {
                        player.sendMessage(TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("PLAYER_OWN_TELL_DEACTIVATED").toString()));
                        return;
                    }

                    int a = 0;
                    String msg = "";
                    for (String s : args) {
                        if (a >= 1) {
                            msg = msg + s + " ";
                        }

                        a++;
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

                    ((BungeePlayerData) MainData.getIns().getPlayerManager().getPlayer(victim.getUniqueId())).setReply(player.getUniqueId());
                    ((BungeePlayerData) MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId())).setReply(victim.getUniqueId());
                } else {

                    player.sendMessage(new ComponentBuilder("Jogador não encontrado.").color(ChatColor.RED).create());
                    return;

                }

            } else {

                sender.sendMessage(new ComponentBuilder("Uso incorreto. Use /tell <nick> <mensagem>").color(ChatColor.YELLOW).create());

            }

        } else {
            sender.sendMessage(new ComponentBuilder("Este comando apenas pode ser executado por um jogador.")
                    .color(ChatColor.RED).create());
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            return ImmutableSet.of();
        }
        List<String> words = new ArrayList<>();
        String text = strings[0];
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.getName().toUpperCase().startsWith(text.toUpperCase())) {
                words.add(p.getName());
            }
        }
        return words;
    }

}
