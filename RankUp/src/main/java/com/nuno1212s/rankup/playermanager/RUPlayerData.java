package com.nuno1212s.rankup.playermanager;

import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * The Player Data for the full pvp server
 */
@Getter
@Setter
public class RUPlayerData extends PlayerData implements ChatData, KitPlayer {

    PlayerGroupData groupData;

    long lastDatabaseAccess, lastGlobalChat, lastLocalChat;

    @Getter
    private Map<Integer, Long> kitUsages;

    volatile long coins;

    public RUPlayerData(PlayerData d) {
        super(d);
        this.coins = 0;
        this.groupData = new PlayerGroupData();
        this.kitUsages = new HashMap<>();
    }

    public synchronized final void setCoins(long coins) {
        this.coins = coins;
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(this));
    }

    public synchronized final long getCoins() {
        return this.coins;
    }

    @Override
    public Group getRepresentingGroup() {
        Group mainGroup = super.getMainGroup();
        if (mainGroup.isOverrides()) {
            return mainGroup;
        }
        return MainData.getIns().getPermissionManager().getGroup(this.groupData.getActiveGroup());
    }

    @Override
    public short getServerGroup() {
        return this.groupData.getActiveGroup();
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        PlayerGroupData.EXTENSION_RESULT extension_result = this.groupData.setCurrentGroup(groupID, duration);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(this));
        return extension_result;
    }

    /**
     * Use the global check expiration method to also check the expiration for the local groups
     * 
     * @param p
     */
    @Override
    public void checkExpiration(Player p) {
        super.checkExpiration(p);
        this.groupData.checkExpiration(p);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            Main.getIns().getMysql().savePlayerData(this);
            c.callback();
        });
    }

    /*
    Chat plugin compat
     */
    @Override
    public long lastGlobalChatUsage() {
        return this.lastGlobalChat;
    }

    @Override
    public void setLastGlobalChatUsage(long time) {
        this.lastGlobalChat = time;
    }

    @Override
    public long lastLocalChatUsage() {
        return this.lastLocalChat;
    }

    @Override
    public void setLastLocalChatUsage(long time) {
        this.lastLocalChat = time;
    }

    /*
    Kit plugin compat
     */
    public void setKitUsages(Map<Integer, Long> kitUsages) {
        this.kitUsages = kitUsages;
    }

    @Override
    public boolean canUseKit(int kitID, long delay) {
        return !this.kitUsages.containsKey(kitID) || this.kitUsages.get(kitID) + delay < System.currentTimeMillis();
    }

    @Override
    public long timeUntilUsage(int kitID, long delay) {
        if (this.kitUsages.containsKey(kitID)) {
            return (this.kitUsages.get(kitID) + delay) - System.currentTimeMillis();
        } else {
            return 0;
        }
    }

    @Override
    public long lastUsage(int kitID) {
        return this.kitUsages.containsKey(kitID) ? this.kitUsages.get(kitID) : 0;
    }

    @Override
    public void registerKitUsage(int kitID, long time) {
        this.kitUsages.put(kitID, time);
    }

    @Override
    public void unregisterKitUsage(int kitID) {
        this.kitUsages.remove(kitID);
    }

    @Override
    public boolean ownsKit(int kitID) {
        //TODO
        return false;
    }
}
