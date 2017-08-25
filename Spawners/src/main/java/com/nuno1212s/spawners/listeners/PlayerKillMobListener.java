package com.nuno1212s.spawners.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.spawners.rewardhandler.RewardManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Handles a player killing another entity
 */
public class PlayerKillMobListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {

        if (Main.getIns().getRewardManager().isInstantReward()) {

            if ((e.getEntity() instanceof Monster) && (e.getDamager() instanceof Player)) {

                if (((Monster) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);
                    e.getEntity().remove();

                    Player killer = (Player) e.getDamager();

                    if (killer == null) {
                        return;
                    }

                    PlayerData d = MainData.getIns().getPlayerManager().getPlayer(killer.getUniqueId());


                    if (MainData.getIns().getServerCurrencyHandler() != null) {
                        RewardManager.EntityType entityType = RewardManager.EntityType.getEntityType((LivingEntity) e.getEntity());
                        long basePrice = Main.getIns().getRewardManager().getRewardPerEntity()
                                .get(entityType);
                        double rankMultiplierForPlayer = RankMultiplierMain.getIns().getRankManager()
                                .getGlobalMultiplier().getRankMultiplierForPlayer(d);
                        long price = (long) Math.floor(basePrice
                                * rankMultiplierForPlayer);

                        com.nuno1212s.spawners.playerdata.PlayerData player = Main.getIns().getPlayerManager().getPlayer(killer.getUniqueId());

                        if (player == null) {
                            player = Main.getIns().getPlayerManager().getPlayerInstance(d.getPlayerID(), rankMultiplierForPlayer);
                        }

                        player.addKill(price);

                        MainData.getIns().getServerCurrencyHandler().addCurrency(d, price);
                        MainData.getIns().getEventCaller().callUpdateInformationEvent(d);
                    }


                }

            }
        }

    }

}
