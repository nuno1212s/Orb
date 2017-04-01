package com.nuno1212s.core.playermanager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.core.main.Main;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.util.BungeeSender;
import com.nuno1212s.core.util.Callback;
import org.bukkit.Bukkit;

import java.util.AbstractMap;
import java.util.UUID;

/**
 * Player data
 */
@RequiredArgsConstructor
@Data
public class PlayerData {

    @NonNull
    UUID id;

    @NonNull
    String name;

    @NonNull
    short groupId;

    @NonNull
    boolean tell, chat;

    @NonNull
    int cash;

    boolean isTeleporting = false;

    AbstractMap.SimpleEntry<Boolean, Long> savedd;

    boolean changedSinceLastSave = false;

    long lastMovement = System.currentTimeMillis();

    public void setChat(boolean b) {
        this.chat = b;
        changedSinceLastSave = true;
    }

    public void setCash(int cash) {
        this.cash = cash;
        changedSinceLastSave = true;
    }

    public void setGroupId(short groupId) {
        this.groupId = groupId;
        changedSinceLastSave = true;
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF(this.id.toString());
        dataOutput.writeShort(groupId);
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "GROUPUPDATE", dataOutput.toByteArray());
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () ->
                MySqlDB.getIns().changeGroup(id, groupId)
        );
    }

    public void teleport(Callback c) {
        this.isTeleporting = true;
        PlayerData d = this;
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            if (changedSinceLastSave) {
                if (!(savedd != null && savedd.getKey() && System.currentTimeMillis() - savedd.getValue() < 1000)) {
                    MySqlDB.getIns().updatePlayerData(d);
                    savedd = new AbstractMap.SimpleEntry<Boolean, Long>(true, System.currentTimeMillis());
                    changedSinceLastSave = false;
                }
            }
            c.callback();
            isTeleporting = false;
        });
    }

    public void setTell(boolean vaule) {
        this.tell = vaule;
        changedSinceLastSave = true;
        BungeeSender.tellNotify(this.name, vaule);
    }

}
