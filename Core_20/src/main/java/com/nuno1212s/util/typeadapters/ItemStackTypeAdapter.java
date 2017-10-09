package com.nuno1212s.util.typeadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {

    @Override
    public ItemStack read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String item = jsonReader.nextString();

        return ItemUtils.itemFrom64(item);
    }

    @Override
    public void write(JsonWriter jsonWriter, ItemStack item) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("itemdata").value(ItemUtils.itemTo64(item));
        jsonWriter.endObject();
    }
}
