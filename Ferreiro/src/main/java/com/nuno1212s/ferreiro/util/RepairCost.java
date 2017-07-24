package com.nuno1212s.ferreiro.util;

import com.nuno1212s.util.Pair;

/**
 * Repair cost
 */
public class RepairCost {

    public static Pair<Integer, Boolean> getRepairCost(int repairTimes) {
        if (repairTimes < 7) {
            return new Pair<>((int) (500 * Math.pow(2, repairTimes)), false);
        } else {
            return new Pair<>((int) 25 + 10 * (repairTimes), true);
        }
    }

}
