package com.nuno1212s.rankup.rankup;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Handles rankup commands
 */
public class RankUpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            RUPlayerData d = (RUPlayerData) MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

            short groupId = d.getGroupID();
            short nextGroup = Main.getIns().getRankUpManager().getNextGroup(groupId);

            Messages messageManager = MainData.getIns().getMessageManager();

            if (nextGroup == -1) {
                messageManager.getMessage("RANK_UP_NO_RANK").sendTo(commandSender);
                return true;
            }

            Group group = MainData.getIns().getPermissionManager().getGroup(nextGroup);
            int groupCost = Main.getIns().getRankUpManager().getGroupCost(group.getGroupID());

            if (d.getCoins() >= groupCost) {
                d.setServerGroup(nextGroup, -1);
                messageManager.getMessage("RANKED_UP")
                        .format("%newRank%", group.getGroupPrefix())
                        .format("%cost%", String.valueOf(groupCost))
                        .sendTo(commandSender);
                Player p = (Player) commandSender;

                Firework spawn = p.getWorld().spawn(p.getLocation(), Firework.class);
                FireworkMeta f = spawn.getFireworkMeta();
                f.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.GREEN).build());
                f.setPower(1);
                spawn.setFireworkMeta(f);

            } else {
                messageManager.getMessage("NO_COINS").sendTo(commandSender);
            }
        }
        return false;
    }
}
