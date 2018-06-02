package com.nuno1212s.rankup.playermanager;

import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.events.PlayerGroupUpdateEvent;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import lombok.Setter;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Player Data for the full pvp server
 */
@Getter
@Setter
public class RUPlayerData extends PlayerData implements ChatData, KitPlayer, EnderChestData {

    PlayerGroupData groupData;

    PlayerGroupData rankUpGroup;

    long lastDatabaseAccess, lastGlobalChat, lastLocalChat;

    @Getter
    private Map<Integer, Long> kitUsages;

    @Getter
    private List<Integer> privateKits;

    volatile long coins;

    private ItemStack[] enderChest;

    public RUPlayerData(PlayerData d, long coins, PlayerGroupData groupData, PlayerGroupData serverGroup, Map<Integer, Long> kitUsages, List<Integer> privateKits, String enderChest) {
        super(d);
        this.coins = coins;
        this.rankUpGroup = serverGroup;
        this.groupData = groupData;
        this.kitUsages = kitUsages;
        this.privateKits = privateKits;
        this.enderChest = EnderChestData.inventoryFromJSON(enderChest);
    }

    public synchronized final void setCoins(long coins) {
        this.coins = coins;
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(this,
                PlayerInformationUpdateEvent.Reason.CURRENCY_UPDATE));
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
    public List<Short> getServerGroups() {
        return Arrays.asList(getServerGroup(), this.rankUpGroup.getActiveGroup());
    }

    public short getRankUpGroup() {
        return this.rankUpGroup.getActiveGroup();
    }

    public PlayerGroupData getRankUpGroupData() {
        return this.rankUpGroup;
    }

    @Override
    public String getNameWithPrefix() {

        if (MainData.getIns().getPermissionManager().getGroup(this.groups.getActiveGroup()).isOverrides()) {
            return super.getNameWithPrefix();
        }

        Group rankUpGroup = MainData.getIns().getPermissionManager().getGroup(this.rankUpGroup.getActiveGroup());

        ClanPlayer player = SimpleClans.getInstance().getClanManager().getClanPlayer(this.getPlayerID());
        String clanTag;

        if (player == null) {
            clanTag = "";
        } else {
            clanTag = ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + player.getTag() + " " + ChatColor.RESET;
        }

        Group groupData = MainData.getIns().getPermissionManager().getGroup(this.groupData.getActiveGroup());

        return groupData.getGroupPrefix() + rankUpGroup.getGroupPrefix() + clanTag + this.getPlayerName();
    }

    @Override
    public PlayerGroupData.EXTENSION_RESULT setServerGroup(short groupID, long duration) {
        Group previousGroup = MainData.getIns().getPermissionManager().getGroup(this.groupData.getActiveGroup()),
                newGroup = MainData.getIns().getPermissionManager().getGroup(groupID);

        PlayerGroupData.EXTENSION_RESULT extension_result = this.groupData.setCurrentGroup(groupID, duration);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(this,
                PlayerInformationUpdateEvent.Reason.GROUP_UPDATE));

        Bukkit.getServer().getPluginManager().callEvent(new PlayerGroupUpdateEvent(this, previousGroup));

        Message updated_group = MainData.getIns().getMessageManager().getMessage("UPDATED_GROUP")
                .format("%playerName%", getPlayerName())
                .format("%newGroup%", newGroup.getGroupPrefix());
        for (PlayerData d : MainData.getIns().getPlayerManager().getPlayers()) {
            updated_group
                    .sendTo(d);
        }

        return extension_result;
    }

    public PlayerGroupData.EXTENSION_RESULT setServerRank(short groupID, long duration) {
        Group previousGroup = MainData.getIns().getPermissionManager().getGroup(this.rankUpGroup.getActiveGroup());

        PlayerGroupData.EXTENSION_RESULT extension_result = this.rankUpGroup.setCurrentGroup(groupID, duration);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(this,
                PlayerInformationUpdateEvent.Reason.GROUP_UPDATE));
        Bukkit.getServer().getPluginManager().callEvent(new PlayerGroupUpdateEvent(this, previousGroup));
        return extension_result;
    }

    /**
     * Use the global check expiration method to also check the expiration for the local groups
     *
     * @param p
     */
    @Override
    public void checkExpiration(PlayerData p) {
        super.checkExpiration(p);
        this.groupData.checkExpiration(p);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {

            MainData.getIns().getMySql().savePlayer(this);

            Main.getIns().getMysql().savePlayerData(this);

            c.callback(null);
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

    @Override
    public boolean shouldReceive() {
        return true;
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
        return this.privateKits.contains(kitID);
    }

    @Override
    public void updateEnderChestData(ItemStack[] items) {
        this.enderChest = items;
    }

    @Override
    public ItemStack[] getEnderChest() {
        return enderChest;
    }
}
