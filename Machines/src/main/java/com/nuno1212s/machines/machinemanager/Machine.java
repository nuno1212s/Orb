package com.nuno1212s.machines.machinemanager;

import com.nuno1212s.machines.players.MachinePlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.Pair;
import lombok.Getter;

import java.util.UUID;

public class Machine {

    @Getter
    private UUID owner;

    @Getter
    private MachineType type;

    @Getter
    private long baseValue;

    @Getter
    private long amount;

    @Getter
    private LLocation machineLocation;

    /**
     * Spacing for the machine to tick.
     * <p>
     * In server ticks (20 = 1 sec)
     */
    @Getter
    private long spacing;

    private transient long currentSpacing;

    public Machine(UUID owner, MachineType type, long baseValue, long amount, LLocation machineLocation, long spacing) {
        this.owner = owner;
        this.type = type;
        this.baseValue = baseValue;
        this.spacing = spacing;
        this.currentSpacing = spacing;
        this.amount = amount;
        this.machineLocation = machineLocation;
    }

    /**
     * Tick this machine.
     * <p>
     * This should be run async
     */
    public void checkTick() {

        switch (type) {

            case ALWAYS_RUNNING: {
                tick();

                break;
            }
            case WHEN_LOADED: {
                if (this.getMachineLocation().getLocation().getChunk().isLoaded()) {
                    tick();
                }
                break;
            }
            case WHEN_ONLINE: {

                PlayerData player = MainData.getIns().getPlayerManager().getPlayer(this.owner);

                if (player != null && player.isPlayerOnServer()) {
                    tick();
                }

                break;
            }
            default: break;
        }

    }

    private void tick() {

        currentSpacing--;

        if (currentSpacing == 0) {
            giveReward();
            this.currentSpacing = spacing;
        }

    }

    private void giveReward() {

        Pair<PlayerData, Boolean> data = MainData.getIns().getPlayerManager().getOrLoadPlayer(this.owner);
        PlayerData player;

        if (data.getKey() == null) {
            return;
        }

        //Assert that the player's server data has been loaded
        if (data.getValue()) {
            player = MainData.getIns().getPlayerManager().requestAditionalServerData(data.getKey());
        } else {
            player = data.getKey();
        }

        MainData.getIns().getServerCurrencyHandler().addCurrency(data.getKey(), this.baseValue * this.amount);

        if (player instanceof MachinePlayer && data.getValue()) {

            MachinePlayer machinePlayer = (MachinePlayer) player;

            machinePlayer.setAmountMadeWhileAway(machinePlayer.getAmountMadeWhileAway() + (baseValue * amount));

        } else if (!data.getValue()) {

            //Player is online, send message
            MainData.getIns().getMessageManager().getMessage("MACHINE_MONEY")
                    .format("%amount%", baseValue * amount)
                    .sendTo(player);
        } else if (data.getValue()) {

            player.save((c) -> {});

        }
    }


    public enum MachineType {

        ALWAYS_RUNNING,
        WHEN_LOADED,
        WHEN_ONLINE

    }

}
