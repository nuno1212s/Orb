package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import lombok.*;

import java.util.UUID;

/**
 * Player data
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class PlayerData {

    protected final UUID playerID;

    @NonNull
    protected short groupID;

    @NonNull
    protected String playerName;

    @NonNull
    protected long cash;

    protected long lastLogin;

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
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }

}
