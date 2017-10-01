package com.nuno1212s.hub.players_toggle;

import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Manages player toggle events
 */
public class PlayerToggleManager {

    @Getter
    private ItemStack playersOn, playersOff;

    public PlayerToggleManager(Module m) {

        File file = m.getFile("hidePlayers.json", true);

        try (Reader r = new FileReader(file)) {

            JSONObject jsonFile = (JSONObject) new JSONParser().parse(r);

            this.playersOn = new SerializableItem((JSONObject) jsonFile.get("PlayersOn"));
            this.playersOff = new SerializableItem((JSONObject) jsonFile.get("PlayersOff"));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Updates the players shown / not shown
     * @param d
     * @param p
     */
    public void updatePlayer(HPlayerData d, Player p) {

        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (player2.getUniqueId().equals(d.getPlayerID())) {
                continue;
            }

            if (!d.isPlayerShown()) {

                if (player2.hasPermission("overrideHide")) {
                    continue;
                }

                p.hidePlayer(player2);
            } else {
                p.showPlayer(player2);
            }

        }


    }

    /**
     * Handle the player joining
     *
     * @param d
     * @param p
     */
    public void handleJoin(HPlayerData d, Player p) {

        boolean shouldHide = !p.hasPermission("overrideHide");

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player.getUniqueId().equals(p.getUniqueId())) {
                continue;
            }

            HPlayerData playerInfo = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

            if (!playerInfo.isPlayerShown() && shouldHide) {
                player.hidePlayer(p);
            }

            if (d.isPlayerShown()) {
                continue;
            }

            if (player.hasPermission("overrideHide")) {
                continue;
            }

            p.hidePlayer(player);

        }

    }

}
