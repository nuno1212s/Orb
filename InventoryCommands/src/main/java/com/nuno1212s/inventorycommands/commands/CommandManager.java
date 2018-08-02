package com.nuno1212s.inventorycommands.commands;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventorycommands.InventoryMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private File commandFile;

    private Map<String, String> connectingInventories;

    public CommandManager(Module module) {

        this.commandFile = new File(module.getDataFolder(), "commands.json");

        if (!commandFile.exists()) {
            module.saveResource(commandFile, "commands.json");
        }

        loadCommands();

    }

    private void loadCommands() {
        this.connectingInventories = new HashMap<>();

        JSONObject json;

        try (FileReader r = new FileReader(this.commandFile)) {

            json = (JSONObject) new JSONParser().parse(r);

            json.forEach((command, inventory) -> {
                connectingInventories.put((String) command, (String) inventory);
            });

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerCommands();
    }

    public void registerCommands() {

        this.connectingInventories.keySet().forEach(InventoryMain.getIns()::registerCommand);

    }

    public void reloadCommands() {
        loadCommands();
    }

    /**
     * Check if a given command has a connection to an inventory
     * @param commandName
     * @return
     */
    public boolean hasInventory(String commandName) {
        return this.connectingInventories.containsKey(commandName);
    }

    /**
     * Get the inventory a command links to
     * @param commandName
     * @return
     */
    public InventoryData getInventory(String commandName) {
        return MainData.getIns().getInventoryManager().getInventory(
                this.connectingInventories.getOrDefault(commandName, ""));
    }

}
