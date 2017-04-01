package com.nuno1212s.core.serverstatus;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Server information
 */
@AllArgsConstructor
@Data
public class ServerInfo {

    String serverName;
    int currentPlayers, maxPlayers;
    Status s;


    public void update(ServerInfo s) {
        this.currentPlayers = s.getCurrentPlayers();
        this.maxPlayers = s.getMaxPlayers();
        this.s = s.getS();
    }

}
