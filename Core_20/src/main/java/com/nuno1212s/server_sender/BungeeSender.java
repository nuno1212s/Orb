package com.nuno1212s.server_sender;

import com.nuno1212s.main.BungeeMain;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeSender {

    private BungeeMain m;

    @Getter
    static BungeeSender ins;

    public BungeeSender(BungeeMain ms) {
        ins = this;
        m = ms;
    }

    public void send(UUID playerID, String serverName, String OGServer) {
        ProxiedPlayer p = m.getProxy().getPlayer(playerID);

        if (p == null) {
            //Player is not in this bungee instance, ignore
            return;
        }

        ServerInfo server = m.getProxy().getServerInfo(serverName);

        if (server == null || !server.canAccess(p)) {
            System.out.println("Failed to find server?");
            MainData.getIns().getServerManager().getSenderRedisHandler().sendResponse(playerID, false, OGServer, "Server does not exist?");
            return;
        }

        p.connect(server, new Callback<Boolean>() {

            @Override
            public void done(Boolean aBoolean, Throwable throwable) {
                System.out.println(throwable != null ? throwable.getMessage() : "");
                MainData.getIns().getServerManager().getSenderRedisHandler().sendResponse(playerID, aBoolean
                        , OGServer
                        , throwable != null ? throwable.getMessage() : "");
            }

        });
    }

}
