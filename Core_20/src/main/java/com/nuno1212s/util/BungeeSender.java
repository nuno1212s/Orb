package com.nuno1212s.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sends bungee information
 */
public class BungeeSender {

    BukkitMain m;

    @Getter
    static BungeeSender ins;

    public BungeeSender(BukkitMain ms) {
        ins = this;
        m = ms;
    }

    public void tellNotify(String name, boolean value) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF(name);
        dataOutput.writeUTF(String.valueOf(value));

        Bukkit.getServer().sendPluginMessage(m, "TELLINFO", dataOutput.toByteArray());
    }

    public void sendPlayer(PlayerData data, Player p, String bungeeServer) {
        Callback c = (o) -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(bungeeServer);
            p.sendPluginMessage(m, "BungeeCord", out.toByteArray());
        };

        data.setShouldSave(false);
        data.save(c);
    }

}
