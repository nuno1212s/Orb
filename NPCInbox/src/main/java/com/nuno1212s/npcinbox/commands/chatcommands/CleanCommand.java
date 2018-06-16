package com.nuno1212s.npcinbox.commands.chatcommands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Reward clean command
 */
public class CleanCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"clean"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward clean";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("rewards.clean")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (Main.getIns().getChatManager().getPlayerMessageBuilder(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You are not creating a reward!");
            return;
        }

        Main.getIns().getChatManager().getPlayerMessageBuilder(player.getUniqueId()).clearMessages();
        player.sendMessage(ChatColor.GREEN + "Messages have been cleared successfully");

    }
}
