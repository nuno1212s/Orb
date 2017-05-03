package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles class editing items
 */
public class ClassEditItemsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"edititems"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/classes edititems <className>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("classes.edititems")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION")
                    .sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        String kitName = args[1];

        Kit classEdit = Main.getIns().getKitManager().getKit(kitName);

        if (classEdit == null) {
            player.sendMessage(ChatColor.RED + "Não existe uma classe com esse nome");
            return;
        }

        player.openInventory(classEdit.getClassEdit());

    }
}
