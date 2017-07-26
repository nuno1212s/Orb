package com.nuno1212s.warps.warpmanager;

import com.nuno1212s.util.SerializableLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 * Warp data class
 */
@Getter
@AllArgsConstructor
public class Warp {

    private String warpName;
    private Location l;
    private String permission;
    private boolean delay, requiredConsole;
    private int delayInSeconds;

    public Warp(JSONObject json) {
        this.warpName = (String) json.get("WarpName");
        this.l = new SerializableLocation((JSONObject) json.get("Location"));
        this.permission = (String) json.get("Permission");
        this.delay = (Boolean) json.get("Delay");
        this.requiredConsole = (Boolean) json.get("RequiredConsole");
        this.delayInSeconds = ((Long) json.get("Delay")).intValue();
    }

    public JSONObject save() {
        JSONObject j = new JSONObject();
        j.put("WarpName", warpName);
        JSONObject object = new JSONObject();
        new SerializableLocation(l).save(object);
        j.put("Location", object);
        j.put("Permission", permission);
        j.put("Delay", this.delay);
        j.put("RequiredConsole", this.requiredConsole);
        j.put("Delay", this.delayInSeconds);
        return j;
    }



}
