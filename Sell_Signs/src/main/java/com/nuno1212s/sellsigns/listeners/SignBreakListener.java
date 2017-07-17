package com.nuno1212s.sellsigns.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.sellsigns.main.Main;
import com.nuno1212s.sellsigns.signs.StoreSign;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Sign break listener
 */
public class SignBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
                StoreSign sign = Main.getIns().getSignManager().getSign(e.getBlock().getLocation());

                if (sign != null) {
                    if (Main.getIns().getSignManager().isEditing(e.getPlayer().getUniqueId())) {
                        Main.getIns().getSignManager().removeSign(sign);
                        MainData.getIns().getMessageManager().getMessage("REMOVED_SIGN").sendTo(e.getPlayer());
                    } else {
                        e.setCancelled(true);
                    }
                }

        }
    }

}
