package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.util.ServerCurrencyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Sell command
 */
public class SellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            long finalPrice = 0;

            double rankMultiplierForPlayer = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(d);

            for (ItemStack content : p.getInventory().getContents()) {
                if (Main.getIns().getRewardManager().getRewardPerItem().containsKey(content.getType())) {
                    long price = Main.getIns().getRewardManager().getRewardPerItem().get(content.getType()) * content.getAmount();


                    finalPrice += (long) Math.floor(price * rankMultiplierForPlayer);
                    p.getInventory().removeItem(content);
                }
            }

            ServerCurrencyHandler sCH = MainData.getIns().getServerCurrencyHandler();
            if (sCH != null) {
                sCH.addCurrency(d, finalPrice);
                MainData.getIns().getMessageManager().getMessage("SOLD_ITEMS")
                        .format("%coins%", String.valueOf(finalPrice))
                        .format("%multiplier%", String.format("%.1f", rankMultiplierForPlayer)).sendTo(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_SERVER_CURRENCY_AVAILABLE").sendTo(p);
            }

            return true;
        }
        return true;
    }
}
