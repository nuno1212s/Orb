package com.nuno1212s.factions.playerdata;

import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.factions.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class FPlayerData extends PlayerData implements ChatData, KitPlayer, EnderChestData {

    @Getter
    private PlayerGroupData serverGroupData;

    @Getter
    @Setter
    private long lastDatabaseAccess, lastGlobalChat, lastLocalChat;

    private volatile long coins;

    @Getter
    private Map<Integer, Long> kitUsages;

    @Getter
    private List<Integer> privateKits;

    private ItemStack[] enderChest;

    public FPlayerData(PlayerData original, PlayerGroupData serverGroup, long coins, Map<Integer, Long> kitUsages, List<Integer> privateKits, String enderChest) {
        super(original);
        this.serverGroupData = serverGroup;
        this.coins = coins;
        this.kitUsages = kitUsages;
        this.privateKits = privateKits;
        this.enderChest = EnderChestData.inventoryFromJSON(enderChest);
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        return serverGroupData.setCurrentGroup(groupID, duration);
    }

    @Override
    public short getServerGroup() {
        return serverGroupData.getActiveGroup();
    }

    @Override
    public Group getRepresentingGroup() {
        Group mainGroup = this.getMainGroup();

        if (mainGroup.isOverrides()) {
            return mainGroup;
        }

        return MainData.getIns().getPermissionManager().getGroup(this.serverGroupData.getActiveGroup());
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            Main.getIns().getMysql().savePlayerData(this);
            c.callback(null);
        });
    }

    public synchronized long getCoins() {
        return coins;
    }

    public synchronized void setCoins(long coins) {
        this.coins = coins;
    }

    /*
     * Chat integration
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

    @Override
    public boolean shouldReceive() {
        return true;
    }

    /*
    Kit integration
     */
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
        return this.privateKits.contains(kitID);
    }

    @Override
    public ItemStack[] getEnderChest() {
        return this.enderChest;
    }

    @Override
    public void updateEnderChestData(ItemStack[] items) {
        this.enderChest = items;
    }
}
