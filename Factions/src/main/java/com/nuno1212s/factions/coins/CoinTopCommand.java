package com.nuno1212s.factions.coins;

import com.nuno1212s.factions.playerdata.FPlayerData;
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

        LinkedHashMap<UUID, Long> dataBaseEntries = new LinkedHashMap<>(); //Main.getIns().getMysql().getCoinTop(10);
        // TODO: 02/10/2017 Add mysql coin top

        List<PlayerData> players = MainData.getIns().getPlayerManager().getPlayers();

        players.sort((o1, o2) -> {
                        if (!(o1 instanceof FPlayerData) || !(o2 instanceof FPlayerData)) {
                            return 0;
                        }

                        long coins1 = ((FPlayerData) o1).getCoins(), coins2 = ((FPlayerData) o2).getCoins();

                        return Long.compare(coins1, coins2);
                }
        );

        if (players.size() > 10) {
            players = players.subList(0, 10);
        }

        players.forEach(player -> dataBaseEntries.put(player.getPlayerID(), ((FPlayerData) player).getCoins()));

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
