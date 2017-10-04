package com.nuno1212s.displays;

import com.nuno1212s.displays.chat.ChatListener;
import com.nuno1212s.displays.chat.ChatManager;
import com.nuno1212s.displays.chat.GlobalChatCommand;
import com.nuno1212s.displays.commands.ChatControlCommand;
import com.nuno1212s.displays.listeners.PlayerJoinListener;
import com.nuno1212s.displays.placeholders.PlaceHolderManager;
import com.nuno1212s.displays.scoreboard.ScoreboardManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * DisplayMain module class
 */
@ModuleData(name = "Displays", version = "1.0", dependencies = {})
public class DisplayMain extends Module {

    @Getter
    static DisplayMain ins;

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

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        registerCommand(new String[]{"g", "global"}, new GlobalChatCommand());
        registerCommand(new String[]{"chat"}, new ChatControlCommand());

        Plugin ins = BukkitMain.getIns();
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), ins);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);

    }

    @Override
    public void onDisable() {

    }
}
