package com.nuno1212s.rankup.crates.commands;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.rankup.crates.Crate;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Crate remove event
 */
public class CrateRemoveCommand implements Command {

    private final ConcurrentMap<Object, Object> map =
            CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build().asMap();

    @Override
    public String[] names() {
        return new String[]{"remove"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate remove <crateName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.remove")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        Crate c = Main.getIns().getCrateManager().getCrate(args[1]);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist!");
            return;
        }

        if (map.containsKey(player.getUniqueId())) {
            Crate cN = (Crate) map.get(player.getUniqueId());
            if (cN.getCrateName().equalsIgnoreCase(c.getCrateName())) {
                Main.getIns().getCrateManager().removeCrate(c);
                player.sendMessage(ChatColor.RED + "A crate foi removida.");
            } else {
                player.sendMessage(ChatColor.RED + "A crate que inseriste não equivale à anterior!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Tens a certeza que queres remover esta crate? Remover esta crate vai tornar todas as chaves desta crate no servidor inuteis.");
            player.sendMessage(ChatColor.RED + "Volta a inserir o comando de novo se quiseres eliminar");
            map.put(player.getUniqueId(), c);
        }

    }
}
