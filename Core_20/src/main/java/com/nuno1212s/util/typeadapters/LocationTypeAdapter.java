package com.nuno1212s.util.typeadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {

    @Override
    public Location read(JsonReader jsonReader) throws IOException {

        jsonReader.beginObject();

        double x = 0, y = 0, z = 0;

        float yaw = 0, pitch = 0;

        String world = null;

        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "X": {
                    x = jsonReader.nextDouble();
                    break;
                }
                case "Y": {
                    y = jsonReader.nextDouble();
                    break;
                }
                case "Z": {
                    z = jsonReader.nextDouble();
                    break;
                }
                case "Yaw": {
                    yaw = ((Double) jsonReader.nextDouble()).floatValue();
                    break;
                }
                case "Pitch": {
                    pitch = ((Double) jsonReader.nextDouble()).floatValue();
                    break;
                }
                case "World": {
                    world = jsonReader.nextString();
                    break;
                }
            }
        }

        jsonReader.endObject();

        World w = Bukkit.getWorld(world);
        if (w == null) {
            w = Bukkit.getWorlds().get(0);
        }

        return new Location(w, x, y, z, yaw, pitch);
    }

    @Override
    public void write(JsonWriter jsonWriter, Location o) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("X").value(o.getX());
        jsonWriter.name("Y").value(o.getY());
        jsonWriter.name("Z").value(o.getZ());
        jsonWriter.name("Yaw").value(o.getYaw());
        jsonWriter.name("Pitch").value(o.getPitch());
        jsonWriter.name("World").value(o.getWorld().getName());
        jsonWriter.endObject();
    }
}
