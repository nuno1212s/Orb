package com.nuno1212s.rankup.economy;

import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Coin top command
 */
class CoinTopCommand {

    static LinkedHashMap<String, Long> getCoinTop() {

        LinkedHashMap<UUID, Long> dataBaseEntries = Main.getIns().getMysql().getCoinTop(10);

        List<PlayerData> players = MainData.getIns().getPlayerManager().getPlayers();

        players.sort((o1, o2) -> {
                        if (!(o1 instanceof PVPPlayerData) || !(o2 instanceof PVPPlayerData)) {
                            return 0;
                        }

                        long coins1 = ((PVPPlayerData) o1).getCoins(), coins2 = ((PVPPlayerData) o2).getCoins();

                        return Long.compare(coins1, coins2);
                }
        );

        if (players.size() > 10) {
            players = players.subList(0, 10);
        }

        players.forEach(player -> dataBaseEntries.put(player.getPlayerID(), ((PVPPlayerData) player).getCoins()));

        LinkedHashMap<UUID, Long> collect = dataBaseEntries.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        LinkedHashMap<String, Long> namesSorted = new LinkedHashMap<>();

        collect.forEach((id, coins) -> {
            PlayerData playerData = MainData.getIns().getMySql().getPlayerData(id, null);
            namesSorted.put(playerData.getNameWithPrefix(), coins);
        });

        return namesSorted;
    }

}
