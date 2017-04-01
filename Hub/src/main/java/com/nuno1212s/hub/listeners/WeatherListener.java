package com.nuno1212s.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Weather listener
 */
public class WeatherListener implements Listener {

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
            e.getWorld().setThundering(false);
            e.getWorld().setStorm(false);
        }
    }

}
