package com.nuno1212s.hub.npcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NPCManager {

    public List<NPC> registeredNPCS;

    public NPCManager(Module m) {
        Gson gson = new GsonBuilder().create();

        File npcFile = m.getFile("", false);
        try (Reader r = new FileReader(npcFile)) {

            Type listType = new TypeToken<ArrayList<NPC>>(){}.getType();

            registeredNPCS = gson.fromJson(r, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the NPCs
     */
    public void updateNPCs() {
        MainData.getIns().getServerManager().fetchServerData();
        registeredNPCS.forEach(NPC::updateNPC);
    }

}
