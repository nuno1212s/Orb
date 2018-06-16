package com.nuno1212s.util.typeadapters;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemStackArrayAdapter extends TypeAdapter<ItemStack[]> {

    @Override
    public ItemStack[] read(JsonReader jsonReader) throws IOException {
        jsonReader.beginArray();

        List<ItemStack> item = new ArrayList<>();
        while (jsonReader.hasNext()) {
            String s = jsonReader.nextString();
            if (s.equalsIgnoreCase("")) {
                item.add(null);
            } else {
                item.add(ItemUtils.itemFrom64(s));
            }
        }

        jsonReader.endArray();
        return item.toArray(new ItemStack[item.size()]);
    }

    @Override
    public void write(JsonWriter jsonWriter, ItemStack[] item) throws IOException {
        jsonWriter.beginArray();
        for (ItemStack itemStack : item) {

            if (itemStack == null) {
                jsonWriter.value("");
                continue;
            }

            jsonWriter.value(ItemUtils.itemTo64(itemStack));
        }

        jsonWriter.endArray();
    }
}
