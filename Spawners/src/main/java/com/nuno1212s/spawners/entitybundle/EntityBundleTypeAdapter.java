package com.nuno1212s.spawners.entitybundle;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.io.IOException;

public class EntityBundleTypeAdapter extends TypeAdapter<EntityBundle> {

    LocationTypeAdapter locationTypeAdapter = new LocationTypeAdapter();

    @Override
    public EntityBundle read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();

        Location location = null;
        int mobCount = 0;
        EntityType type = EntityType.PIG;

        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "Last-Known-Location": {
                    LLocation read = locationTypeAdapter.read(jsonReader);

                    World world = Bukkit.getWorld(read.getWorld());
                    if (world == null) {
                        return null;
                    }

                    location = read.getLocation();
                    break;
                }
                case "EntityType": {
                    String entity = jsonReader.nextString();
                    type = EntityType.valueOf(entity);

                    break;
                }
                case "MobCount": {
                    mobCount = jsonReader.nextInt();
                    break;
                }
            }
        }

        return new EntityBundle(type, location, mobCount);
    }

    @Override
    public void write(JsonWriter jsonWriter, EntityBundle entityBundle) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("Last-Known-Location");

        Location location = entityBundle.getEntity().getLocation();

        locationTypeAdapter.write(jsonWriter, new LLocation(location));

        jsonWriter.name("EntityType").value(entityBundle.getEntity().getType().name());
        jsonWriter.name("MobCount").value(entityBundle.getMobCount());
        jsonWriter.endObject();
    }
}
