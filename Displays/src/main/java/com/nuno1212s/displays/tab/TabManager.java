package com.nuno1212s.displays.tab;

import com.nuno1212s.modulemanager.Module;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.inventivetalent.tabapi.TabAPI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TabManager {

    private String topDisplay, bottomDisplay;

    public TabManager(Module m) {
        File file = m.getFile("tabConfig.json", true);

        try (FileReader reader = new FileReader(file)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);

            this.topDisplay = ChatColor.translateAlternateColorCodes('&',
                    (String) jsonObject.getOrDefault("TopDisplay", "&fInferis \\n Beta"));
            this.bottomDisplay = ChatColor.translateAlternateColorCodes('&',
                    (String) jsonObject.getOrDefault("BottomDisplay", "&cTeste"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param p
     */
    public void sendDisplay(Player p) {
        TabAPI.setHeaderFooter(p, topDisplay, bottomDisplay);
    }

}
