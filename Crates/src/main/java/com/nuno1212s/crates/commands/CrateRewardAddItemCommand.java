package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.crates.crates.Reward;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CrateRewardAddItemCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"additemtoreward"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate additemtoreward <crateName> <rewardID>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.reward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        String crateName = args[1];

        Crate c = Main.getIns().getCrateManager().getCrate(crateName);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "The crate with the name does not exist");
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You are not holding an item in your hand");
            return;
        }

        int rewardID;

        try {
            rewardID = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The reward ID must be a number");
            return;
        }

        Reward r = c.getReward(rewardID);

        if (r == null) {
            player.sendMessage(ChatColor.RED + "There is no reward with that id");
            return;
        }

        r.getItems().add(player.getItemInHand().clone());
        player.sendMessage(ChatColor.RED + "Added to the reward items");
    }
}
