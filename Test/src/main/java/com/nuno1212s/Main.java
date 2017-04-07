package com.nuno1212s;


import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

/**
 * Main module class
 */
@ModuleData(name = "Test", version = "1.0", dependencies = {})
public class Main extends Module implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        registerCommand(new String[]{"test"}, this);
        Bukkit.getServer().getPluginManager().registerEvents(this, com.nuno1212s.main.Main.getIns());
        System.out.println("Test is being enabled");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {


    }

    @Override
    public void onDisable() {
        System.out.println("Test is being disabled");
    }

    @EventHandler
    public void onCore(CoreLoginEvent e) {

    }


}
