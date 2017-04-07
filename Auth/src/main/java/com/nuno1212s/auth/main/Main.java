package com.nuno1212s.auth.main;

import com.nuno1212s.auth.hooks.AuthMeHook;
import com.nuno1212s.auth.listener.MessageListener;
import com.nuno1212s.auth.util.BungeeSender;
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
        new BungeeSender(com.nuno1212s.main.Main.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(hook, com.nuno1212s.main.Main.getIns());
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(com.nuno1212s.main.Main.getIns(), "AUTOLOGIN", new MessageListener(this));
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(com.nuno1212s.main.Main.getIns(), "AUTOLOGIN");
    }

    @Override
    public void onDisable() {

    }
}
