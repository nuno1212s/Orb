package com.nuno1212s.playermanager;

import com.nuno1212s.permissions.PermissionsGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import com.nuno1212s.permissions.PermissionsAPI;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerData {

    /**
     * Name of the player
     */
    String name;

    /**
     * UUID of the player
     */
    UUID uuid;

    /**
     * ID of the group the player is in
     */
    short groupId;

    private boolean premium;

    private String lastIp;

    private long lastLogin;

    private boolean tell;

    public String getPlayerName() {
        return name;
    }

    public synchronized void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public synchronized boolean isPremium() {
        return premium;
    }

    public synchronized void setPremium(boolean premium) {
        this.premium = premium;
    }

    public synchronized String getLastIp() {
        return lastIp;
    }

    public synchronized void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public synchronized long getLastLogin() {
        return lastLogin;
    }

    public synchronized void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean hasPermission(String permission) {
        PermissionsGroup group = PermissionsAPI.getIns().getGroup(getGroupId());
        return group.hasPermission(permission);
    }

    public String getNameWithPrefix() {
        return ChatColor.translateAlternateColorCodes('&', PermissionsAPI.getIns().getGroup(getGroupId()).getPrefix() + getName());
    }

    public synchronized boolean getTell() {
        return tell;
    }

    public synchronized void setTell(boolean vaule) {
        this.tell = vaule;
    }

}
