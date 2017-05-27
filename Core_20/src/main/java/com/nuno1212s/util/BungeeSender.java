package com.nuno1212s.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.BukkitMain;
import org.bukkit.Bukkit;

/**
 * Created by COMP on 19/08/2016.
 */
public class BungeeSender {

    static BukkitMain m;

    public BungeeSender(BukkitMain ms) {
        m = ms;
    }

    public static void tellNotify(String name, boolean value) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF(name);
        dataOutput.writeUTF(String.valueOf(value));

        Bukkit.getServer().sendPluginMessage(m, "TELLINFO", dataOutput.toByteArray());
    }

}
