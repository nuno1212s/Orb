package com.nuno1212s.factions.main;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.displays.placeholders.PlaceHolder;
import com.nuno1212s.factions.coins.CoinCommand;
import com.nuno1212s.factions.events.*;
import com.nuno1212s.factions.miningworld.MiningWorld;
import com.nuno1212s.factions.miningworld.commands.MiningWorldCommand;
import com.nuno1212s.factions.mysql.MySql;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.bukkit.ChatColor;


@ModuleData(name = "Factions", version = "0.1", dependencies = {"Boosters", "Displays", "Classes"})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private MySql mysql;

    @Getter
    private MiningWorld miningWorld;

    @Override
    public void onEnable() {
        ins = this;
        mysql = new MySql();
        miningWorld = new MiningWorld(this);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new MCMMOExperienceListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new FactionCreateListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PowerChangeListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), ins);

        DisplayMain.getIns().getPlaceHolderManager().registerPlaceHolder("%coins%", (player) ->
                String.valueOf(((FPlayerData) player).getCoins())
        );

        DisplayMain.getIns().getPlaceHolderManager().registerPlaceHolder("%faction%", (player) -> {
                    Faction faction = MPlayer.get(player.getPlayerID()).getFaction();

                    if (FactionColl.get().getNone().equals(faction)) {
                        return ChatColor.GRAY + "Sem Faction";
                    }

                    return faction.getName();
                }
        );

        DisplayMain.getIns().getPlaceHolderManager().registerPlaceHolder("%power%", (d) ->
                String.format("%.2f", MPlayer.get(d.getPlayerID()).getPower())
        );

        DisplayMain.getIns().getPlaceHolderManager().registerPlaceHolder("%factionTag%", (player) -> {
            Faction faction = MPlayer.get(player.getPlayerID()).getFaction();

            if (FactionColl.get().getNone().equals(faction)) {
                return "";
            }

            return faction.getName();
        });

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());
        registerCommand(new String[]{"minar"}, new MiningWorldCommand());

    }

    @Override
    public void onDisable() {
        miningWorld.saveToFile();
    }
}
