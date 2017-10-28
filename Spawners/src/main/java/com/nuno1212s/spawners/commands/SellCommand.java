package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.util.ServerCurrencyHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.ListIterator;

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
            int amount = 0;

            double rankMultiplierForPlayer = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(d);

            ListIterator<ItemStack> iterator = p.getInventory().iterator();
            while (iterator.hasNext()) {
                ItemStack content = iterator.next();

                if (content == null || content.getType() == Material.AIR) {
                    continue;
                }

                if (Main.getIns().getRewardManager().getRewardPerItem().containsKey(content.getType())) {
                    long price = Main.getIns().getRewardManager().getRewardPerItem().get(content.getType()) * content.getAmount();

                    amount += content.getAmount();

                    finalPrice += (long) Math.floor(price * rankMultiplierForPlayer);
                    iterator.set(new ItemStack(Material.AIR));
                }
            }

            if (finalPrice == 0) {
                //No items have been sold
                MainData.getIns().getMessageManager().getMessage("NO_ITEMS_SOLD").sendTo(p);
                return true;
            }

            ServerCurrencyHandler sCH = MainData.getIns().getServerCurrencyHandler();
            if (sCH != null) {
                sCH.addCurrency(d, finalPrice);
                MainData.getIns().getEventCaller().callUpdateInformationEvent(d);
                MainData.getIns().getMessageManager().getMessage("SOLD_ITEMS")
                        .format("%amount%", NumberFormat.getInstance().format(amount))
                        .format("%coins%", NumberFormat.getInstance().format(finalPrice))
                        .format("%multiplier%", String.format("%.1f", rankMultiplierForPlayer)).sendTo(p);
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_SERVER_CURRENCY_AVAILABLE").sendTo(p);
            }

        }
        return true;
    }
}
