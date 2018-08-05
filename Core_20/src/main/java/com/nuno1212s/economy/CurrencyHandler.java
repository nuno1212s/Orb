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

        return CompletableFuture.supplyAsync(() -> loadAndGetCurrency(playerData), MainData.getIns().getAsyncExecutor());
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
        }, MainData.getIns().getAsyncExecutor());
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

    /**
     * Load aditional server information if needed and adds the coins to the player
     * <p>
     * Does not need to explicitly save the player information because since the PlayerData is already provided we can assume that it is
     * either loaded and saved somewhere else, or the player is on the server
     *
     * @param data  The PlayerData
     * @param coins The coins to add
     */
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

            boolean successFull = ((ServerCurrency) data).removeCurrency(coins);

            if (successFull)
                MainData.getIns().getEventCaller().callUpdateInformationEvent(data,
                        PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE);

            return CompletableFuture.completedFuture(successFull);
        }

        return CompletableFuture.supplyAsync(() -> loadAndRemoveCurrency(data, coins)
                , MainData.getIns().getAsyncExecutor());
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
        }, MainData.getIns().getAsyncExecutor());

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
