package com.nuno1212s.displays.placeholders;

import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles placeholders
 */
public class PlaceHolderManager {

    private Map<String, PlaceHolder> placeHolders;

    public PlaceHolderManager() {
        placeHolders = new HashMap<>();

        placeHolders.put("%playerName%", new PlaceHolder() {
            @Override
            public String replacePlaceHolder(PlayerData d) {
                return d.getNameWithPrefix();
            }
        });

        placeHolders.put("%online%", new PlaceHolder() {
            @Override
            public String replacePlaceHolder(PlayerData d) {
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            }
        });
    }

    public void registerPlaceHolder(String placeHolder, PlaceHolder holder) {
        this.placeHolders.put(placeHolder, holder);
    }

    public String format(String message, PlayerData player) {
        for (Map.Entry<String, PlaceHolder> placeHolders : placeHolders.entrySet()) {
            message = message.replace(placeHolders.getKey(), placeHolders.getValue().replacePlaceHolder(player));
        }
        return message;
    }

}
