package com.nuno1212s.bungee.motd;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Handles server MOTD
 */
public class ServerMOTD implements Listener {

    private File dataFile;

    @Getter
    private Map<Integer, String> motds;

    @Getter
    private List<Timer> activeTimers;

    public ServerMOTD(File dataFolder) {

        dataFile = new File(dataFolder, "motd.json");

        Tick t = new Tick(this);
        MainData.getIns().getScheduler().runTaskTimer(t, 0, 20);

        reloadConfig();

    }

    public void reloadConfig() {

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            motds = new HashMap<>();
            activeTimers = new ArrayList<>();

        } else {
            JSONObject data;
            try (FileReader reader = new FileReader(dataFile)) {
                data = (JSONObject) new JSONParser().parse(reader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return;
            }

            this.motds = new HashMap<>();
            this.activeTimers = new ArrayList<>();

            JSONObject motds = (JSONObject) data.get("MOTDS");
            motds.forEach((key, value) -> {
                this.motds.put(Integer.parseInt((String) key), (String) value);
            });

            JSONArray objects = (JSONArray) data.get("Timers");
            objects.forEach(timer -> {
                this.activeTimers.add(Timer.fromJSON((JSONObject) timer));
            });

        }
    }

    public int addMOTD(String MOTD) {
        //this.currentMOTD = MOTD;
        this.motds.put(this.motds.size() + 1, MOTD);
        return this.motds.size();
    }

    public boolean removeMOTD(int id) {
        if (!this.motds.containsKey(id)) {
            return false;
        }

        this.motds.remove(id);
        HashMap<Integer, String> motds = new HashMap<>(this.motds);

        this.motds.clear();

        motds.forEach((motdid, motd) -> {
            this.motds.put(this.motds.size() + 1, motd);
        });

        return true;

    }

    public void addTimer(Timer t) {
        this.activeTimers.add(t);
    }

    public void endTimer(Timer t) {
        this.activeTimers.remove(t);

        //Remove motds

        this.motds.entrySet().forEach(motd -> {
            if (motd.getValue().contains(t.getTimerSignature())) {
                removeMOTD(motd.getKey());
            }
        });

    }

    public String getCurrentMOTD() {
        String s = this.motds.get(new Random().nextInt(this.motds.size()));

        for (Timer activeTimer : this.activeTimers) {
            if (s.contains(activeTimer.getTimerSignature())) {
                s = s.replace(activeTimer.getTimerSignature(), activeTimer.toTime());
            }
        }

        return s;
    }

    public void save() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONObject data = new JSONObject();

        JSONObject motds = new JSONObject();

        this.motds.forEach((motdid, motd) -> {
            motds.put(String.valueOf(motdid), motd);
        });

        JSONArray timers = new JSONArray();

        this.activeTimers.forEach(timer -> {
            timers.add(timer.toJSON());
        });

        data.put("MOTDS", motds);
        data.put("Timers", timers);

        try (Writer writer = new FileWriter(this.dataFile)) {
            data.writeJSONString(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        ServerPing s = e.getResponse();
        s.setDescription(this.getCurrentMOTD());

        e.setResponse(s);
    }

}

@Getter
@Setter
class Timer {

    private String timerSignature;

    private long lastTime, startTime;


    public static Timer fromJSON(JSONObject json) {
        Timer t = new Timer();
        t.setTimerSignature((String) json.get("TimerSig"));
        t.setLastTime((Long) json.get("LastTime"));
        t.setStartTime((Long) json.get("StartTime"));
        return t;
    }

    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        data.put("TimerSig", this.timerSignature);
        data.put("LastTime", this.lastTime);
        data.put("StartTime", this.startTime);
        return data;
    }

    public String toTime() {
        long timeLeft = startTime + lastTime - System.currentTimeMillis();

        return new TimeUtil("HH:MM:SS").toTime(timeLeft);
    }

}

@AllArgsConstructor
class Tick implements Runnable {

    @Getter
    ServerMOTD manager;

    @Override
    public void run() {
        List<Timer> activeTimers = new ArrayList<>(manager.getActiveTimers());
        activeTimers.forEach(timer -> {
            if (timer.getStartTime() + timer.getLastTime() < System.currentTimeMillis()) {
                manager.endTimer(timer);
            }
        });
    }
}