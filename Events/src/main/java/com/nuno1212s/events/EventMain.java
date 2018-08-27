package com.nuno1212s.events;

import com.nuno1212s.events.war.WarEventScheduler;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

@ModuleData(name = "Events", version = "1.0-BETA", dependencies = {"Clans"})
public class EventMain extends Module {

    @Getter
    static EventMain ins;

    @Getter
    private WarEventScheduler warEvent;

    @Override
    public void onEnable() {
        ins = this;


    }

    @Override
    public void onDisable() {

    }
}
