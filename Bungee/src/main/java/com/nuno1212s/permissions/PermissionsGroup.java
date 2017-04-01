package com.nuno1212s.permissions;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class PermissionsGroup {


    private String ServerName;
    private String ServerType;
    private short GroupId;
    private boolean IsDefault;
    private String Display;
    private String Prefix;
    private String Suffix;
    private HashMap<String, Boolean> Permissions;

    public PermissionsGroup(String ServerName, String ServerType, short GroupId, boolean IsDefault, String Display, String Prefix, String Suffix, HashMap<String, Boolean> Permissions) {

        this.ServerName = ServerName;
        this.ServerType = ServerType;
        this.GroupId = GroupId;
        this.Permissions = Permissions;
        this.IsDefault = IsDefault;
        this.Display = Display;
        this.Prefix = Prefix;
        this.Suffix = Suffix;

    }

    public boolean isDefault() {
        return IsDefault;
    }

    public short getGroupId() {
        return this.GroupId;
    }

    public boolean hasPermission(String permission) {
        if (Permissions.isEmpty()) {
            return false;
        }
        if (Permissions.containsKey("*")) {
            if (Permissions.get("*") == true)
                return true;
        }
        if (!Permissions.containsKey(permission))
            return false;
        return Permissions.get(permission);
    }

    public String getPrefix() {
        return this.Prefix;
    }
}
