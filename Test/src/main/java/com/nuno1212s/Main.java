package com.nuno1212s;


import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.playermanager.PlayerData;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Main module class
 */
@ModuleData(name = "Test", version = "1.0", dependencies = {})
public class Main extends Module implements Listener {

    @Override
    public void onEnable(Plugin enabler) {
        Bukkit.getServer().getPluginManager().registerEvents(this, enabler);
        System.out.println("Test is being enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("Test is being disabled");
    }

    @EventHandler
    public void onCore(CoreLoginEvent e) {
        System.out.println(e.getPlayerInfo());
        e.setPlayerInfo(new PlayerDataPVP(e.getPlayerInfo()));
    }

}

@ToString
class PlayerDataPVP extends PlayerData {

    String cona;

    public PlayerDataPVP(PlayerData d) {
        super(d.getPlayerID(), d.getGroupID(), d.getPlayerName(), d.getCash());
        cona = "True";
    }



}