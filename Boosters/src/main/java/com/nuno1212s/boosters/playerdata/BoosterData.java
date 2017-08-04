package com.nuno1212s.boosters.playerdata;

import java.util.List;

/**
 * Booster data
 */
public interface BoosterData {

    List<String> getBoosters();

    void addBooster(String boosterID);

    void removeBooster(String boosterID);

}
