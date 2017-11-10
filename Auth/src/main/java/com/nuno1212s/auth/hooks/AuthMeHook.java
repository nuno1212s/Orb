package com.nuno1212s.auth.hooks;


import com.google.common.cache.CacheBuilder;
import com.nuno1212s.auth.main.Main;
import com.nuno1212s.auth.util.BungeeSender;
import com.nuno1212s.auth.util.Callback;
import com.nuno1212s.main.MainData;
import fr.xephi.authme.api.NewAPI;
import fr.xephi.authme.events.LoginEvent;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Handles authme hooks
 */
public class AuthMeHook implements Listener {
    Map<Object, Object> needLogin = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build().asMap();

    Main m;

    public AuthMeHook(Main m) {
        this.m = m;
    }

    /**
     * Will need this
     *
     * @param e
     */
    @EventHandler
    public void onJoin(LoginEvent e) {
        if (needLogin.containsKey(e.getPlayer().getUniqueId())) {
            ((Callback) needLogin.get(e.getPlayer().getUniqueId())).onLogin();
            needLogin.remove(e.getPlayer().getUniqueId());
            return;
        }

        BungeeSender.loginNotify(e.getPlayer().getName());
        MainData.getIns().getScheduler().runTaskLater(() -> {
            e.getPlayer().setWalkSpeed(.2f);
            e.getPlayer().setFlySpeed(.1f);
        }, 2L);
    }

    public void requestLogin(Player p, Callback c) {
        needLogin.put(p.getUniqueId(), c);
    }

    public boolean isPlayerRegistered(Player p) {
        return NewAPI.getInstance().isRegistered(p.getName());
    }

    public void forceRegister(Player p) {
        if (!NewAPI.getInstance().isAuthenticated(p)) {
            NewAPI.getInstance().forceRegister(p, RandomStringUtils.random(8, true, true));
            MainData.getIns().getScheduler().runTaskLater(() ->
                    p.sendMessage(ChatColor.RED + "Foi logado automaticamente."), 2L);
        }
    }

    public void forceLogin(Player p) {
        if (!NewAPI.getInstance().isAuthenticated(p)) {
            NewAPI.getInstance().forceLogin(p);
            MainData.getIns().getScheduler().runTaskLater(() ->
                    p.sendMessage(ChatColor.RED + "Foi logado automaticamente."), 2L);
        }
    }
}