package com.nuno1212s.crates.TypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuno1212s.util.typeadapters.ItemStackArrayAdapter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStackListAdapter extends TypeAdapter<List<ItemStack>> {

    ItemStackArrayAdapter itemStack = new ItemStackArrayAdapter();

    @Override
    public List<ItemStack> read(JsonReader jsonReader) throws IOException {
        ArrayList<ItemStack> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(itemStack.read(jsonReader)));
        return objects;
    }

    @Override
    public void write(JsonWriter jsonWriter, List<ItemStack> itemStacks) throws IOException {
        itemStack.write(jsonWriter, itemStacks.toArray(new ItemStack[itemStacks.size()]));
    }
}
