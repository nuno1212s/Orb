package com.nuno1212s.tradewindow.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.trades.Trade;
import com.nuno1212s.tradewindow.trades.TradeRequest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command is only for players");

            return true;
        }

        if (args.length < 1) {

            commandSender.sendMessage("/trade <jogador>");

            return true;
        }

        Player player1 = (Player) commandSender;

        Player player2 = Bukkit.getServer().getPlayer(args[0]);

        if (player2 == null || !player2.isOnline()) {

            MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_ONLINE").sendTo(commandSender);

            return true;
        }

        if (player1.getUniqueId().equals(player2.getUniqueId())) {
            MainData.getIns().getMessageManager().getMessage("CANNOT_TRADE_SELF")
                    .sendTo(player1);
            return true;
        }

        if (TradeMain.getIns().getTradeManager().hasTradeRequestFrom(player1.getUniqueId(), player2.getUniqueId())) {

            TradeRequest tradeRequest = TradeMain.getIns().getTradeManager().getTradeRequest(player1.getUniqueId(), player2.getUniqueId());

            Trade trade = tradeRequest.createTrade();

            if (trade == null) {

                MainData.getIns().getMessageManager().getMessage("TRADE_EXPIRED").sendTo(commandSender);

                return true;
            }

        } else {

            if (TradeMain.getIns().getTradeManager().hasTradeRequestFrom(player2.getUniqueId(), player1.getUniqueId())
                    && !TradeMain.getIns().getTradeManager().getTradeRequest(player2.getUniqueId(), player1.getUniqueId()).hasExpired()) {

                MainData.getIns().getMessageManager().getMessage("TRADE_REQUEST_NOT_EXPIRED")
                        .sendTo(player1);

                return true;
            }

            TradeMain.getIns().getTradeManager().registerTradeRequest(player1.getUniqueId(), player2.getUniqueId());

            MainData.getIns().getMessageManager().getMessage("TRADE_REQUEST_SENT").sendTo(player1);

            String trade_request_received = MainData.getIns().getMessageManager().getMessage("TRADE_REQUEST_RECEIVED")
                    .format("%player%", player1.getName())
                    .toString();

            BaseComponent[] messageComponents = TextComponent.fromLegacyText(trade_request_received);

            for (BaseComponent baseComponent : messageComponents) {
                baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + player1.getName()));
            }

            player2.spigot().sendMessage(messageComponents);

        }

        return true;
    }
}
