package com.nuno1212s.boosters.main;

import com.nuno1212s.boosters.boosters.BoosterManager;
import com.nuno1212s.boosters.redis.RedisListener;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.rediscommunication.RedisReceiver;
import lombok.Getter;

/**
 * Handles main classes
 */
@ModuleData(name = "Boosters", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    BoosterManager boosterManager;

    @Getter
    RedisListener redisHandler;

    @Override
    public void onEnable() {
        ins = this;
        boosterManager = new BoosterManager();
    }

    @Override
    public void onDisable() {

    }
}
