package com.nuno1212s.events;

import com.nuno1212s.events.war.WarEventScheduler;
import com.nuno1212s.events.war.commands.*;
import com.nuno1212s.events.war.listeners.*;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;

@ModuleData(name = "Events", version = "1.0-BETA", dependencies = {"Clans"})
public class EventMain extends Module {

    @Getter
    static EventMain ins;

    @Getter
    private WarEventScheduler warEvent;

    @Override
    public void onEnable() {
        ins = this;

        warEvent = new WarEventScheduler(this);

        Bukkit.getServer().getPluginManager().registerEvents(warEvent.getSelectPlayersInventory(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerConnectListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinClanListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeaveClanListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new ClanDisbandListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDeathListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new RespawnListener(), BukkitMain.getIns());

        MainData.getIns().getScheduler().runTaskTimer(warEvent::checkTime, 20, 20);

        addMessageFile();

        registerCommand(new String[]{"registarclan"}, new RegisterCommand());
        registerCommand(new String[]{"entrarevento"}, new JoinCommand());
        registerCommand(new String[]{"sairevento"}, new LeaveCommand());
        registerCommand(new String[]{"setfallbacklocation"}, new SetFallbackLocationCommand());
        registerCommand(new String[]{"setspectatorlocation"}, new SetSpectatorLocationCommand());
        registerCommand(new String[]{"addspawnlocation"}, new AddSpawnLocationCommand());
        registerCommand(new String[]{"schedulestart"}, new ScheduleStartSoon());

    }

    private void addMessageFile() {

        File f = new File(getDataFolder(), "messages.json");

        if (!f.exists()) {
            saveResource(f, "messages.json");
        }

        MainData.getIns().getMessageManager().addMessageFile(f);
    }

    @Override
    public void onDisable() {
        if (this.warEvent.getOnGoing() != null) {
            this.warEvent.getOnGoing().forceEnd();
        }

        this.warEvent.save();
    }
}
