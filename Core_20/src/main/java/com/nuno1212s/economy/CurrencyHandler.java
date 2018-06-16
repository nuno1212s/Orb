package com.nuno1212s.economy;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CurrencyHandler {

    /**
     * Get the server currency for the player
     *
     * @param playerData The player to get the currency for
     * @return
     */
    public CompletableFuture<Long> getCurrency(PlayerData playerData) {
        if (playerData instanceof ServerCurrency) {
            return CompletableFuture.completedFuture(((ServerCurrency) playerData).getCurrency());
        }

        return CompletableFuture.supplyAsync(() -> loadAndGetCurrency(playerData));
    }

    private long loadAndGetCurrency(PlayerData playerData) {
        if (playerData instanceof ServerCurrency) {
            return ((ServerCurrency) playerData).getCurrency();
        }

        PlayerData player = MainData.getIns().getPlayerManager().requestAditionalServerData(playerData);

        if (player instanceof ServerCurrency) {

            return ((ServerCurrency) player).getCurrency();

        }

        return 0L;
    }

    public CompletableFuture<Long> getCurrency(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            Pair<PlayerData, Boolean> playerData = MainData.getIns().getPlayerManager().getOrLoadPlayer(player);

            if (playerData.getKey() != null) {
                return loadAndGetCurrency(playerData.getKey());
            }

            return 0L;
        });
    }

    /**
     * Add an amount of coins to a player
     *
     * @param player
     * @param coins
     */
    public CompletableFuture<Void> addCurrency(PlayerData player, long coins) {
        if (player instanceof ServerCurrency) {
            ((ServerCurrency) player).addCurrency(coins);

            MainData.getIns().getEventCaller().callUpdateInformationEvent(player, PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);

            return CompletableFuture.completedFuture(null);
        } else {

            return CompletableFuture.runAsync(() -> loadAndAddCurrency(player, coins));

        }
    }

    public CompletableFuture<Void> addCurrency(UUID player, long coins) {

        return CompletableFuture.runAsync(() -> {

            Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(player);

            if (orLoadPlayer.getKey() != null) {

                loadAndAddCurrency(orLoadPlayer.getKey(), coins);

                if (!orLoadPlayer.getValue()) {

                    MainData.getIns().getEventCaller().callUpdateInformationEvent(orLoadPlayer.getKey(),
                            PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);

                } else {
                    //No need to call update player info, because player is offline

                    //If the player has been loaded the data should be saved
                    orLoadPlayer.getKey().save((o) -> {
                    });
                }
            }

        });

    }

    private void loadAndAddCurrency(PlayerData data, long coins) {

        if (data instanceof ServerCurrency) {
            ((ServerCurrency) data).addCurrency(coins);
            return;
        }

        PlayerData player = MainData.getIns().getPlayerManager().requestAditionalServerData(data);

        if (player instanceof ServerCurrency) {
            ((ServerCurrency) player).addCurrency(coins);
        }

    }

    public CompletableFuture<Boolean> removeCurrency(PlayerData data, long coins) {
        if (data instanceof ServerCurrency) {

            boolean b = ((ServerCurrency) data).removeCurrency(coins);

            MainData.getIns().getEventCaller().callUpdateInformationEvent(data,
                    PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);

            return CompletableFuture.completedFuture(b);
        }

        return CompletableFuture.supplyAsync(() -> {
            return loadAndRemoveCurrency(data, coins);
        });
    }

    public CompletableFuture<Boolean> removeCurrency(UUID player, long coins) {

        return CompletableFuture.supplyAsync(() -> {

            Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(player);

            if (orLoadPlayer.getKey() != null) {

                boolean b = loadAndRemoveCurrency(orLoadPlayer.getKey(), coins);

                if (!orLoadPlayer.getValue()) {

                    MainData.getIns().getEventCaller().callUpdateInformationEvent(orLoadPlayer.getKey(),
                            PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);

                } else {

                    orLoadPlayer.getKey().save((o) -> {
                    });

                }

                return b;
            }

            return false;
        });

    }

    private boolean loadAndRemoveCurrency(PlayerData data, long coins) {
        if (data instanceof ServerCurrency) {
            return ((ServerCurrency) data).removeCurrency(coins);
        }

        data = MainData.getIns().getPlayerManager().requestAditionalServerData(data);

        if (data instanceof ServerCurrency) {
            return ((ServerCurrency) data).removeCurrency(coins);
        }

        return false;
    }

}
