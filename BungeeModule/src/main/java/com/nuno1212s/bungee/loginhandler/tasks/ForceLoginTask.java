package com.nuno1212s.bungee.loginhandler.tasks;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.main.Main;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * Force login into servers
 */
public class ForceLoginTask implements Runnable {

    private Main m;

    private ProxiedPlayer p;

    private Server s;

    public ForceLoginTask(Main m, ProxiedPlayer p, Server s) {
        this.m = m;
        this.p = p;
        this.s = s;
    }

    @Override
    public void run() {

        try {
            PendingConnection connection = p.getPendingConnection();

            if (!p.isConnected()) {
                System.out.println("Player left the server.");
                return;
            }

            //Premium player
            if (connection.isOnlineMode()) {
                sendBukkitMessage(p.getName());
            } else {
                //Cracked player
                if (SessionHandler.getIns().getSession(p.getUniqueId()) != null && SessionHandler.getIns().getSession(p.getUniqueId()).isAuthenticated()) {
                    sendBukkitMessage(p.getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBukkitMessage(String playername) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        //subchannel
        dataOutput.writeUTF("AUTOLOGIN");

        dataOutput.writeUTF(playername);

        if (s != null) {
            s.sendData("AUTOLOGIN", dataOutput.toByteArray());
        }

    }

}
