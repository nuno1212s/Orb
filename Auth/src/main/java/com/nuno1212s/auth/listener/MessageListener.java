package com.nuno1212s.auth.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.auth.main.Main;
import com.nuno1212s.main.BukkitMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Handles the listening of messages from the bungee server.
 */
public class MessageListener implements PluginMessageListener {

    private Main m;

    public MessageListener(Main m) {
        this.m = m;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

        if (s.equalsIgnoreCase("AUTOLOGIN")) {
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(bytes);
            if (dataInput.readUTF().equalsIgnoreCase("AUTOLOGIN")) {
                String playerName = dataInput.readUTF();
                Player p = Bukkit.getPlayer(playerName);

                if (p == null) {
                    return;
                }

                if (Main.hook.isPlayerRegistered(player)) {
                    Main.hook.forceLogin(player);
                } else {
                    Main.hook.forceRegister(player);
                }

            }
        } else if (s.equalsIgnoreCase("REQUESTLOGIN")) {
            Main.hook.requestLogin(player, () -> {
                player.kickPlayer(ChatColor.GRAY + "Updating all your information \n" + ChatColor.RED + "Please rejoin in a minute.\n" + ChatColor.GOLD + "Thank you for your patience.");
                ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

                dataOutput.writeUTF("AUTHENTICATE");
                dataOutput.writeUTF(player.getName());

                player.sendPluginMessage(BukkitMain.getIns(), "AUTOLOGIN", dataOutput.toByteArray());
            });
        }
    }
}