package com.nuno1212s.machines.machinemanager;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuno1212s.util.LLocation;

import java.io.IOException;
import java.util.UUID;

public class MachineGSONTP extends TypeAdapter<Machine> {

    @Override
    public void write(JsonWriter jsonWriter, Machine machine) throws IOException {

        jsonWriter.beginObject();

        jsonWriter.name("Owner").value(machine.getOwner().toString());
        jsonWriter.name("MType").value(machine.getType().name());
        jsonWriter.name("BaseValue").value(machine.getBaseValue());
        jsonWriter.name("Amount").value(machine.getAmount());

        jsonWriter.name("Location").beginObject();

        LLocation l = machine.getMachineLocation();

        jsonWriter.name("X").value(l.getBlockX());
        jsonWriter.name("Y").value(l.getBlockY());
        jsonWriter.name("Z").value(l.getBlockZ());
        jsonWriter.name("World").value(l.getWorld());

        jsonWriter.endObject();

        jsonWriter.name("Spacing").value(machine.getSpacing());

        jsonWriter.endObject();

    }

    @Override
    public Machine read(JsonReader jsonReader) throws IOException {

        jsonReader.beginObject();

        Machine m;

        UUID owner;


        while (jsonReader.hasNext()) {

            String s = jsonReader.nextName();

            switch (s) {

                case "Owner":


            }

        }

        return null;
    }
}
