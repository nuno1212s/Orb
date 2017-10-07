package com.nuno1212s.warps.warpmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;
import com.nuno1212s.warps.commands.WarpCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Warp
 */
public class WarpManager {

    @Getter
    List<Warp> warps;

    private File f;

    @Getter
    @Setter
    private WarpCommand command;

    private Gson gson;

    public WarpManager(Module m) {
        this.f = m.getFile("warps.json", false);

        this.gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationTypeAdapter()).create();
        command = new WarpCommand();
        load(f);
    }

    /**
     *
     * @param f
     */
    private void load(File f) {
        try (FileReader r = new FileReader(f)) {


            Type listType = new TypeToken<ArrayList<Warp>>(){}.getType();

            this.warps = this.gson.fromJson(r, listType);

            if (this.warps == null) {
                this.warps = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Warp w : this.warps) {
            registerCommand(w.getWarpName());
        }
    }

    public void save() {

        try (FileWriter r = new FileWriter(f)) {
            gson.toJson(this.warps, r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerWarp(String name, Location l, boolean delay, int delaySeconds, boolean console){
        if (getWarp(name) == null) {
            Warp w = new Warp(name, l.clone(), "warp." + name, delay, console, delaySeconds);

            if (!w.isRequiredConsole()) {
                registerCommand(w.getWarpName());
            }

            this.warps.add(w);
        }

    }

    public Warp getWarp(String name) {
        for (Warp warp : this.warps) {
            if (warp.getWarpName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public void removeWarp(Warp w) {
        this.warps.remove(w);

    }

    public void registerCommand(String... aliases) {
        BukkitMain ins = BukkitMain.getIns();
        PluginCommand command = getCommand(aliases[0], ins);

        command.setAliases(Arrays.asList(aliases));
        getCommandMap().register(BukkitMain.getIns().getDescription().getName(), command);
        BukkitMain.getIns().getCommand(aliases[0]).setExecutor(this.command);
    }


    private Field field;
    private Constructor<PluginCommand> c;

    private PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            if (c == null) {
                c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                c.setAccessible(true);
            }

            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }


    private CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                if (field == null) {
                    field = SimplePluginManager.class.getDeclaredField("commandMap");
                    field.setAccessible(true);
                }

                commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }



}
