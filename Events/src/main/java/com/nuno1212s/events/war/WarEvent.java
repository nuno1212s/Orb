package com.nuno1212s.events.war;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarEvent {

    private Map<String, List<UUID>> players;

    public WarEvent(Map<String, List<UUID>> players) {
        this.players = players;


    }

}
