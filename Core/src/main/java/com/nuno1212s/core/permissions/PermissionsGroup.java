package com.nuno1212s.core.permissions;

import java.util.HashMap;

import lombok.Data;

@Data
public class PermissionsGroup {

    private String ServerName;
    private String ServerType;
    private short GroupId;
    private boolean IsDefault, overridesServerGroup;
    private String Display;
    private String Prefix;
    private String Suffix;
    private String scoreboardName;
    private HashMap<String, Boolean> Permissions;

    public PermissionsGroup(String ServerName, String ServerType, short GroupId, boolean IsDefault, String Display, String Prefix, String Suffix, HashMap<String, Boolean> Permissions, boolean overridesServerGroup, String scoreboardDisplay) {

        this.ServerName = ServerName;
        this.ServerType = ServerType;
        this.GroupId = GroupId;
        this.Permissions = Permissions;
        this.IsDefault = IsDefault;
        this.Display = Display;
        this.Prefix = Prefix;
        this.Suffix = Suffix;
        this.overridesServerGroup = overridesServerGroup;
        this.scoreboardName = scoreboardDisplay;

    }

    public boolean isDefault() {
        return IsDefault;
    }

    public short getGroupId() {
        return this.GroupId;
    }

    public void setServerName(String server) {this.ServerName = server; };

    public void setPrefix(String prefix) {
        if (prefix.length() > 2)
            prefix = prefix + " ";
        this.Prefix = prefix;
    }

    public void setSuffix(String suffix) {
        if (suffix.length() > 2)
            suffix = suffix + " ";
        this.Suffix = suffix;
    }

}
