package com.nuno1212s.auth.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.Main;
import org.bukkit.Bukkit;

public class BungeeSender {

    static Main m;

    public BungeeSender(Main ms) {
        m = ms;
    }

    public static void loginNotify(String name) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("LOGIN");
        dataOutput.writeUTF(name);
        Bukkit.getServer().sendPluginMessage(m, "AUTOLOGIN", dataOutput.toByteArray());
    }
}