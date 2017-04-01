package com.nuno1212s.hub.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.core.serverstatus.Status;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.messagemanager.Messages;
import com.nuno1212s.hub.servermanager.NovusServer;
import com.nuno1212s.hub.servermanager.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NpcRightClick implements Listener {
    public Main plugin;

    public NpcRightClick(Main pl) {
        this.plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNPCClickk(NPCRightClickEvent e) {
        Player p = e.getClicker();

        if (!e.getNPC().data().has("Connect"))
            return;

        String bungeeid = e.getNPC().data().get("Connect");

        PlayerData d = PlayerManager.getIns().getPlayerData(e.getClicker().getUniqueId());
        if (d.isTeleporting()) {
            return;
        }

        NovusServer s = ServerManager.getIns().getServerByBungeeID(bungeeid);

        if (s.getInfo().getS() == Status.OFFLINE) {
            p.sendMessage(Messages.getIns().getMessage("ServerOffline", "&cO servidor está offline!"));
            return;
        }

        if (s.getInfo().getCurrentPlayers() >= s.getInfo().getMaxPlayers() && !p.hasPermission("novus.joinfull")) {
            p.sendMessage(Messages.getIns().getMessage("ServerFull", "&6Servidor cheio... Compra VIP para teres slot reservado."));
            return;
        }

        d.teleport((o) -> {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                out.writeUTF("Connect");
                out.writeUTF(bungeeid);

                p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


    }

}
