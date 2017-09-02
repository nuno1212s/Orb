package com.nuno1212s.npcinbox.commands.chatcommands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.chat.MessageBuilder;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Command to remove the last added message to a rewards message builder
 */
public class RLastCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"rlast"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward rlast";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("rewards.rlast")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        MessageBuilder playerMessageBuilder = Main.getIns().getChatManager().getPlayerMessageBuilder(player.getUniqueId());
        if (playerMessageBuilder == null) {
            player.sendMessage(ChatColor.RED + "You are not currently creating a reward");
            return;
        }

        playerMessageBuilder.deleteLastMessage();
        player.sendMessage(ChatColor.GREEN + "Removed the last message successfully");

    }
}
