package com.nuno1212s.clans.listeners;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getEntity().getUniqueId());

        if (playerData instanceof ClanPlayer) {

            if (((ClanPlayer) playerData).hasClan()) {
                Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

                c.incrementDeaths();
            }

            ((ClanPlayer) playerData).setDeaths(((ClanPlayer) playerData).getDeaths() + 1);

        }

        if (e.getEntity().getKiller() != null) {

            PlayerData killer = MainData.getIns().getPlayerManager().getPlayer(e.getEntity().getKiller().getUniqueId());

            if (killer == null) {
                return;
            }

            if (killer instanceof ClanPlayer) {

                if (((ClanPlayer) killer).hasClan()) {
                    Clan clan = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) killer).getClan());

                    clan.incrementKills();
                }

                ((ClanPlayer) killer).setKills(((ClanPlayer) killer).getKills() + 1);
            }

        }

    }

}
