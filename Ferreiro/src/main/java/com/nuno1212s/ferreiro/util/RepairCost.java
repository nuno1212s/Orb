package com.nuno1212s.ferreiro.util;

import com.nuno1212s.util.Pair;

/**
 * Repair cost
 */
public class RepairCost {

    private static int BASE_AMOUNT_COINS = 500, BASE_AMOUNT_CASH = 25;

    /**
     *
     * @param repairTimes
     * @return Integer - The amount of currency
     *         Boolean - If server currency or global currency should be used (true = global currency (cash), false = server currency (coins))
     */
    public static Pair<Integer, Boolean> getRepairCost(int repairTimes) {
        if (repairTimes < 7) {
            return new Pair<>((int) (BASE_AMOUNT_COINS * Math.pow(2, repairTimes)), false);
        } else {
            return new Pair<>((int) BASE_AMOUNT_CASH + 10 * (repairTimes), true);
        }
    }

}
