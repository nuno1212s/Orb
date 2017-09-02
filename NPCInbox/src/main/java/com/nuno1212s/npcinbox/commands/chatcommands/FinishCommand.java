package com.nuno1212s.npcinbox.commands.chatcommands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.chat.MessageBuilder;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by COMP on 02/09/2017.
 */
public class FinishCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"finish"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward finish";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("rewards.createreward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        MessageBuilder playerMessageBuilder = Main.getIns().getChatManager().getPlayerMessageBuilder(player.getUniqueId());
        if (playerMessageBuilder == null) {
            player.sendMessage(ChatColor.RED + "You are not currently creating a reward");
            return;
        }

        MainData.getIns().getRewardManager().createReward(playerMessageBuilder.buildReward());

    }
}
