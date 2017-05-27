package com.nuno1212s.auth.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.BukkitMain;
import org.bukkit.Bukkit;

public class BungeeSender {

    static BukkitMain m;

    public BungeeSender(BukkitMain ms) {
        m = ms;
    }

    public static void loginNotify(String name) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("LOGIN");
        dataOutput.writeUTF(name);
        Bukkit.getServer().sendPluginMessage(m, "AUTOLOGIN", dataOutput.toByteArray());
    }
}