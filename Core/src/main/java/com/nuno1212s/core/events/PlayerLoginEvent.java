package com.nuno1212s.core.events;

import com.nuno1212s.core.permissions.PlayerPermissions;
import net.md_5.bungee.api.ChatColor;
import com.nuno1212s.core.configmanager.MainConfig;
import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.permissions.PermissionsGroupManager;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.core.util.Title;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles the player join event
 */
public class PlayerLoginEvent implements Listener{
	private List<String> header, footer;
	public PlayerLoginEvent() {
		this.header = MainConfig.getIns().getHeader();
		this.footer = MainConfig.getIns().getFooter();
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoinEvent(PlayerJoinEvent e) {

        sendTab(e.getPlayer());
        
    }

    @EventHandler
    public void onLogin(org.bukkit.event.player.PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPlayedBefore()) {
            modifyHasPlayedBefore(e.getPlayer());
        }

        PlayerData playerData = MySqlDB.getIns().getPlayerData(p.getUniqueId(), p.getName());
        if (playerData == null) {
            playerData = new PlayerData(e.getPlayer().getUniqueId(), e.getPlayer().getName(), PermissionsGroupManager.getIns().getDefault().getGroupId(), true, true, 0);
        }

        PlayerManager.getIns().addPlayer(playerData);
        if (Main.getInstance().getEvent() != null) {
            Main.getInstance().getEvent().onLogin(e);
        }
        if (Main.getInstance().getServerPermissions() == null) {
            PlayerPermissions.getIns().registerPlayer(e.getPlayer());
        }
        if (e.getResult() == org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL) {
            if (p.hasPermission("novus.joinfull")) {
                e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED);
            } else {
                e.setKickMessage(ChatColor.RED + "Servidor cheio... Compra VIP para teres slot reservado.");
                if (Main.getInstance().getEvent() != null) {
                    Main.getInstance().getEvent().forceSave(e.getPlayer().getUniqueId());
                }
                PlayerManager.getIns().removePlayer(playerData);
            }
        }
    }

    private Class c;

    private Field f;

    private void modifyHasPlayedBefore(Player p) {
        try {
            if (c == null) {
                c = Class.forName("org.bukkit.craftbukkit."+ Main.getInstance().getVersion() + "entity.CraftPlayer");
            }
            if (f == null) {
                f = c.getDeclaredField("hasPlayedBefore");
                f.setAccessible(true);
            }
            f.set(p, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTab(Player p) {
    	String h = "";
    	String f = "";
    	for (String a : header)
    		if (!a.equalsIgnoreCase(header.get(0)))
    			h = h + "\n" + a;
    		else
    			h = a;
    	for (String b : footer)
    		if (!b.equalsIgnoreCase(footer.get(0)))
    			f = f + "\n" + b;
    		else
    			f = b;
    	Title.sendTabTitle(p, ChatColor.translateAlternateColorCodes('&', h), ChatColor.translateAlternateColorCodes('&', f));
    }

}
