package com.nuno1212s.auth.main;

import com.nuno1212s.auth.hooks.AuthMeHook;
import com.nuno1212s.auth.listener.MessageListener;
import com.nuno1212s.auth.util.BungeeSender;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import org.bukkit.Bukkit;

/**
 * Main module class
 */
@ModuleData(name = "Auth", version = "1.0", dependencies = {})
public class Main extends Module {

    public static AuthMeHook hook;

    @Override
    public void onEnable() {
        hook = new AuthMeHook(this);
        new BungeeSender(BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(hook, BukkitMain.getIns());
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(BukkitMain.getIns(), "AUTOLOGIN", new MessageListener(this));
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(BukkitMain.getIns(), "AUTOLOGIN");
    }

    @Override
    public void onDisable() {

    }
}
