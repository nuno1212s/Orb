package com.nuno1212s.hub.listeners;

import com.nuno1212s.core.permissions.PermissionsGroup;
import com.nuno1212s.core.permissions.PlayerPermissions;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.messagemanager.Messages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerChat implements Listener {
    public Main plugin;

    public PlayerChat(Main pl) {
        this.plugin = pl;
    }

    private HashMap<UUID, Long> lastTimeChat = new HashMap<>();

    private long time = 3000;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        Player p = e.getPlayer();

        String msg = e.getMessage();

        if (lastTimeChat.containsKey(e.getPlayer().getUniqueId())) {
            if (time + lastTimeChat.get(e.getPlayer().getUniqueId()) > System.currentTimeMillis() && !p.hasPermission("novus.bypass")) {
                p.sendMessage(Messages.getIns().formatMessage(Messages.getIns().getMessage("CHAT_NOT_YET", "&cAinda não podes falar no chat. Faltam %seconds% segundo(s)"), new AbstractMap.SimpleEntry<>("%seconds%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.time + lastTimeChat.get(e.getPlayer().getUniqueId()) - System.currentTimeMillis())))));
                return;
            }
        }

        lastTimeChat.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

        if (p.hasPermission("novus.chatcolor")) {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        }

        PermissionsGroup pg = PlayerPermissions.getIns().getGroup(p);

        String name = pg.getPrefix() + p.getDisplayName() + pg.getSuffix();

        ChatColor cc = ChatColor.WHITE;
        if (pg.isDefault())
            cc = ChatColor.GRAY;

        for (Player o : Bukkit.getOnlinePlayers())
            if (PlayerManager.getIns().getPlayerData(o.getUniqueId()).isChat())
                o.sendMessage(name + ChatColor.GRAY + ": " + cc + msg);

    }

}
