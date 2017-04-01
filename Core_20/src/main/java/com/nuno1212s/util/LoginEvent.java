package com.nuno1212s.util;

import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * Login event.
 */
public interface LoginEvent {

    void onLogin(PlayerLoginEvent e);

    void forceSave(UUID u);

}
