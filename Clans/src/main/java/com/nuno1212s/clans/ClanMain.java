package com.nuno1212s.clans;

import com.nuno1212s.clans.chathandler.ChatRequests;
import com.nuno1212s.clans.clanmanager.ClanManager;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.clans.commands.ClanCommand;
import com.nuno1212s.clans.inventories.InventoryManager;
import com.nuno1212s.clans.listeners.PlayerDeathListener;
import com.nuno1212s.clans.mysql.MySQLHandler;
import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.displays.placeholders.PlaceHolderManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;

@ModuleData(name = "Clans", version = "1.0-SNAPSHOT", dependencies = {"Displays"})
public class ClanMain extends Module {

    @Getter
    static ClanMain ins;

    @Getter
    private MySQLHandler mySQLHandler;

    @Getter
    private ChatRequests chatRequests;

    @Getter
    private ClanManager clanManager;

    @Getter
    private InventoryManager inventoryManager;

    public void onEnable() {
        ins = this;

        this.mySQLHandler = new MySQLHandler();
        this.chatRequests = new ChatRequests();
        this.clanManager = new ClanManager();
        this.inventoryManager = new InventoryManager(this);

        registerCommand(new String[]{"clan"}, new ClanCommand());

        Bukkit.getServer().getPluginManager().registerEvents(chatRequests, BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), BukkitMain.getIns());

        PlaceHolderManager placeHolderManager = DisplayMain.getIns().getPlaceHolderManager();

        placeHolderManager.registerPlaceHolder("%clan%", (player) -> {

            if (player instanceof ClanPlayer) {

                if (((ClanPlayer) player).hasClan()) {
                    return getClanManager().getClan(((ClanPlayer) player).getClan()).getClanName();
                }

            }

            return MainData.getIns().getMessageManager().getMessage("NO_CLAN").toString();
        });

        placeHolderManager.registerPlaceHolder("%KDR%", (player) -> {

            if (player instanceof ClanPlayer) {

                return String.format(".%2f", ((float) ((ClanPlayer) player).getKills()) / ((ClanPlayer) player).getDeaths());

            }

            return "N/A";
        });

        placeHolderManager.registerPlaceHolder("%KDD%", (player) -> {

            if (player instanceof ClanPlayer) {

                return String.valueOf(((ClanPlayer) player).getKills() - ((ClanPlayer) player).getDeaths());

            }

            return "N/A";

        });

        placeHolderManager.registerPlaceHolder("%KILLS%", (player) -> {
            if (player instanceof ClanPlayer) {

                return String.valueOf(((ClanPlayer) player).getKills());

            }

            return "N/A";
        });

        placeHolderManager.registerPlaceHolder("%DEATHS%", (player) -> {
            if (player instanceof ClanPlayer) {

                return String.valueOf(((ClanPlayer) player).getDeaths());

            }

            return "N/A";
        });

    }

    public void onDisable() {

    }
}
