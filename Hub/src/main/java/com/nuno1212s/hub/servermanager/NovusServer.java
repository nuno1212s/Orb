package com.nuno1212s.hub.servermanager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.nuno1212s.core.serverstatus.ServerInfo;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

@Data
@RequiredArgsConstructor
public class NovusServer {

    @NonNull
    ServerInfo info;

    @NonNull
    String displayName, configuratioName;

    NPC npc;

    Hologram h;

    public String getStatus() {
        String serverS = ServerManager.getIns().status.get(this.info.getS());
        serverS = serverS.replace("{ONLINE}", String.valueOf(this.info.getCurrentPlayers()));
        return ChatColor.translateAlternateColorCodes('&', serverS);
    }

}
