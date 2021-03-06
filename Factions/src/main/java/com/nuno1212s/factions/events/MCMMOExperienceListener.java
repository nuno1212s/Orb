package com.nuno1212s.factions.events;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles mcmmo experience boosters
 */
public class MCMMOExperienceListener implements Listener {

    @EventHandler
    public void onExpReceive(McMMOPlayerXpGainEvent e) {
        PlayerData data = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        double multiplier = Main.getIns().getBoosterManager().getFinalMultiplierForPlayer(data);
        float xpGained = e.getRawXpGained(), newXP;
        newXP = (int) Math.floor(xpGained * multiplier);
        e.setRawXpGained(newXP);
        MainData.getIns().getMessageManager().getMessage("RECEIVED_MCMMO_XP")
                .format("%xp%", String.valueOf(newXP))
                .format("%skill%", LocaleLoader.getString(StringUtils.getCapitalized(e.getSkill().name()) + ".SkillName"))
                .format("%multiplier%", String.format("%.2f", multiplier))
                .sendTo(e.getPlayer());
    }

}
