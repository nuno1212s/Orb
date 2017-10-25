package com.nuno1212s.spawners.entitybundle;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.io.IOException;

public class EntityBundleTypeAdapter extends TypeAdapter<EntityBundle> {

    @Override
    public EntityBundle read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();

        Location location = null;
        int mobCount = 0;
        EntityType type = EntityType.PIG;

        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "Last-Known-Location": {
                    jsonReader.beginObject();
                    double x = jsonReader.nextDouble(), y = jsonReader.nextDouble(), z = jsonReader.nextDouble();
                    String world = jsonReader.nextString();

                    World world1 = Bukkit.getWorld(world);
                    if (world1 == null) {
                        return null;
                    }

                    location = new Location(world1, x, y, z);

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
        jsonWriter.name("Last-Known-Location").beginObject();

        Location location = entityBundle.getEntity().getLocation();

        jsonWriter.name("X").value(location.getX());
        jsonWriter.name("Y").value(location.getY());
        jsonWriter.name("Z").value(location.getZ());
        jsonWriter.name("World").value(location.getWorld().getName());

        jsonWriter.endObject();
        jsonWriter.name("EntityType").value(entityBundle.getEntity().getType().name());
        jsonWriter.name("MobCount").value(entityBundle.getMobCount());
        jsonWriter.endObject();
    }
}
