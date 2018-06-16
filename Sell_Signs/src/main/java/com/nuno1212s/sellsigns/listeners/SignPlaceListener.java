package com.nuno1212s.sellsigns.listeners;

import com.nuno1212s.sellsigns.main.Main;
import com.nuno1212s.sellsigns.signs.StoreSign;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Handles the creation of signs
 */
public class SignPlaceListener implements Listener {

    @EventHandler
    public void onPlayer(SignChangeEvent e) {
        if (e.getLine(0).contains("[Loja]")) {
            if (Main.getIns().getSignManager().isEditing(e.getPlayer().getUniqueId())) {

                int buyPrice = 0;
                boolean canBuy;
                try {
                    buyPrice = Integer.parseInt(e.getLine(1));
                    canBuy = true;
                } catch (NumberFormatException ex) {
                    canBuy = false;
                }

                int sellPrice = 0;
                boolean canSell;
                try {
                    sellPrice = Integer.parseInt(e.getLine(2));
                    canSell = true;
                } catch (NumberFormatException ex) {
                    canSell = false;
                }

                int nextID = Main.getIns().getSignManager().getNextID();
                StoreSign sign = new StoreSign(nextID, e.getBlock().getLocation().clone(), buyPrice, sellPrice, canSell, canBuy);
                Main.getIns().getSignManager().addSign(sign);
                e.getPlayer().sendMessage(ChatColor.RED + "You created a store sign");
            }
        }
    }

}
