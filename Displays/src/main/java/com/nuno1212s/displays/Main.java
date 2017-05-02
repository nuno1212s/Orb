package com.nuno1212s.displays;

import com.nuno1212s.displays.chat.ChatListener;
import com.nuno1212s.displays.chat.ChatManager;
import com.nuno1212s.displays.placeholders.PlaceHolderManager;
import com.nuno1212s.displays.scoreboard.ScoreboardManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Main module class
 */
@ModuleData(name = "Displays", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private ScoreboardManager scoreboardManager;

    @Getter
    private PlaceHolderManager placeHolderManager;

    @Getter
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        ins = this;
        placeHolderManager = new PlaceHolderManager();
        chatManager = new ChatManager();
        scoreboardManager = new ScoreboardManager(getFile("scoreboard.json", true));

        Plugin ins = com.nuno1212s.main.Main.getIns();
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), ins);

    }

    @Override
    public void onDisable() {

    }
}
