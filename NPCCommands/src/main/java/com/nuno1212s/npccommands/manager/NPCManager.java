package com.nuno1212s.npccommands.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class NPCManager {

    private Map<UUID, List<String>> npcCommands;

    private File dataFile;

    private Gson gson;

    public NPCManager(Module m) {
        gson = new GsonBuilder().create();

        dataFile = m.getFile("data.json", false);

        try (FileReader r = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, List<String>>>() {}.getType();

            this.npcCommands = gson.fromJson(r, type);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (this.npcCommands == null) {
                this.npcCommands = new HashMap<>();
            }
        }

    }

    /**
     * Save the NPC commands
     */
    public void saveNPCs() {

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Writer wr = new FileWriter(this.dataFile)) {
            gson.toJson(this.npcCommands, wr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the commands linked to the NPC
     *
     * @param npcID The ID of the npc
     * @param p The player to use the commands on
     */
    public void executeCommands(UUID npcID, Player p) {
        if (this.npcCommands.containsKey(npcID)) {
            List<String> commands = this.npcCommands.get(npcID);

            commands.forEach(p::performCommand);

        }
    }

    /**
     * Link a command to an NPC
     *
     * @param npcID The ID of the NPC
     * @param command The command to link
     */
    public void addCommandToNPC(UUID npcID, String command) {
        List<String> commands = this.npcCommands.getOrDefault(npcID, new ArrayList<>());
        commands.add(command);

        this.npcCommands.put(npcID, commands);

    }

    /**
     * Remove a command from an NPC
     *
     * @param npcID The ID of the NPC
     * @param command The command to remove
     * @return True if the command was successfully removed, false if not
     */
    public boolean removeCommandFromNPC(UUID npcID, String command) {

        if (!npcCommands.containsKey(npcID)) {
            return false;
        }

        List<String> strings = npcCommands.get(npcID);

        Iterator<String> iterator = strings.iterator();

        while (iterator.hasNext()) {
            String next = iterator.next();

            if (next.toLowerCase().startsWith(command.toLowerCase())) {
                iterator.remove();
                return true;
            }

        }

        return false;
    }

    /**
     * Get the commands from an NPC
     *
     * @param npcID The ID of the NPC
     * @return
     */
    public List<String> getCommandsFromNPC(UUID npcID) {
        return this.npcCommands.get(npcID);
    }

}
