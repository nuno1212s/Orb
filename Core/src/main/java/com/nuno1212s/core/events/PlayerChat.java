package com.nuno1212s.core.events;

import com.nuno1212s.core.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    public static boolean canChat = true;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {

        if (!canChat) {
            if (!e.getPlayer().hasPermission("novus.core.bypasschatoff")) {
                e.setCancelled(true);
                Main.getIns().getMessages().getMessage("ChatOff").sendTo(e.getPlayer());
            }
        }

    }

}
