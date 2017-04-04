package com.nuno1212s.bungee.events;

import com.nuno1212s.playermanager.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Event;

/**
 * Bungee core login
 */
@AllArgsConstructor
public class BungeeCoreLogin extends Event {

    @Getter
    @Setter
    private PlayerData data;

}
