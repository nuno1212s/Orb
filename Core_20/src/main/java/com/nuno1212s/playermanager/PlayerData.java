package com.nuno1212s.playermanager;

import com.nuno1212s.main.Main;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Player data
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    final UUID playerID;

    @NonNull
    short groupID;

    @NonNull
    String playerName;

    @NonNull
    long cash;

    /**
     * All classes that extend Player Data and have their independent server groups
     * should implement this method
     */
    public short getServerGroup() {
        return -1;
    }

    /**
     * All classes that extend Player Data should override this method and do their own
     * form of saving player data
     *
     * @param c The callback for when it is done saving
     */
    public void save(Callback c) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getIns(), () -> {
            Main.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }

}
