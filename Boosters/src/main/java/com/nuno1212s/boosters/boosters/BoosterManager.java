package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages all boosters
 */
public class BoosterManager {

    //TODO: The boosters must store the ID of their owner, because they need to be globally accessible

    @Getter
    private final List<Booster> boosters;

    public BoosterManager() {
        boosters = Collections.synchronizedList(new ArrayList<>());
        addBoosters(Main.getIns().getMysqlHandler().loadBoosters());
    }

    private void addBoosters(List<Booster> boosters) {
        for (Booster booster : boosters) {
            if (booster.isApplicable(null)) {
                this.boosters.add(booster);
            }
        }
    }

    public void loadBoostersForPlayer(UUID player) {

        this.boosters.addAll(Main.getIns().getMysqlHandler().loadBoosters(player));
    }

    private String getRandomID() {
        String random = RandomStringUtils.random(5, true, true);
        if (getBooster(random) != null) {
            return getRandomID();
        }
        return random;
    }

    public Booster createBooster(UUID owner, float multiplier, long durationInMillis, BoosterType type, String applicableServer, String customName) {
        return new Booster(getRandomID(), owner, type, multiplier, durationInMillis, 0, false, applicableServer, customName);
    }

    public void handleBoosterExpiration(Booster b) {
        //TODO: Announce booster expiration

        notifyPlayer(b, b.getOwner());
        Main.getIns().getRedisHandler().handleBoosterDeletion(b);
    }

    public void notifyPlayer(Booster b, UUID player) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            return;
        }

        MainData.getIns().getMessageManager().getMessage("BOOSTER_FINISHED")
                .format("%boosterName%", b.getCustomName()).sendTo(p);

        removeBooster(b);

    }

    public void handleBoosterAdition(Booster b) {
        this.boosters.add(b);
    }

    public void activateBooster(Booster b) {
        b.activate();
        Main.getIns().getRedisHandler().handleBoosterActivation(b);
    }

    public void addBooster(Booster b) {
        this.boosters.add(b);
        Main.getIns().getRedisHandler().addBooster(b);
    }

    public void removeBooster(Booster b) {
        this.boosters.remove(b);
        Main.getIns().getMysqlHandler().removeBooster(b.getBoosterID());
    }

    public List<Booster> getBoosterForPlayer(UUID player) {
        return this.boosters.stream().filter(b -> b.getOwner() != null && b.getOwner().equals(player)).collect(Collectors.toList());
    }

    public Booster getBooster(String boosterID) {
        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.getBoosterID().equalsIgnoreCase(boosterID)) {
                    return booster;
                }
            }
            return null;
        }
    }


    public double getMCMMOMultiplierForPlayer(PlayerData data) {
        double currentBooster = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(data);

        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable(data.getPlayerID())) {
                    currentBooster += booster.getMultiplier();
                }
            }
        }

        return currentBooster;
    }

    public boolean isBoosterActive(PlayerData data) {

        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.isApplicable(data.getPlayerID()) && booster.isActivated()) {
                    return true;
                }
            }
        }

        return false;
    }

}
