package com.nuno1212s.npcinbox.commands.chatcommands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles the command canceling
 */
public class CancelCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"cancel"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward cancel";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("rewards.cancel")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (Main.getIns().getChatManager().getPlayerMessageBuilder(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You are not currently creating a reward");
            return;
        }

        Main.getIns().getChatManager().unregisterPlayer(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Successfully canceled.");

    }
}
