package com.nuno1212s.util.NBTDataStorage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NMS Compound version safe
 *
 * @author Nuno Neto
 */
public class NBTCompound {

    @Getter
    Map<String, Object> values = new HashMap<>();

    //TODO: FIX ME TO WORK THE NEW WAYYYYYYYY
    //FIXME

    /**
     * NBTTagCompound Reflection pointer
     */
    @Getter
    Object nbtTagCompound;

    ReflectionManager reflectionManager;

    public NBTCompound(ItemStack i) {
        reflectionManager = ReflectionManager.getIns();
        /**
         * Get NMS version of item stack
         */
        Class craftBukkit = reflectionManager.getClass(reflectionManager.CRAFT_BUKKIT + "inventory.CraftItemStack");

        //Copy item NMS method
        Method asNMSCopy = reflectionManager.getMethod(craftBukkit, "asNMSCopy", ItemStack.class);

        //Copied NMS item
        Object nmsItem = reflectionManager.invokeMethod(asNMSCopy, null, i);

        //NMSItemStack
        Class nmsItemStack = reflectionManager.getClass(reflectionManager.NMS + "ItemStack");
        /**
         * Get the NBTTagCompound of the item
         */
        Method getTag = reflectionManager.getMethod(nmsItemStack, "getTag");

        if (nmsItem == null) {

            Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

            this.nbtTagCompound = reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtDataCompound));

            this.values = loadFromNBTData(this.nbtTagCompound);

            return;
        }

        //NBT Tag data
        this.nbtTagCompound = reflectionManager.invokeMethod(getTag, nmsItem);

        if (nbtTagCompound == null) {
            Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

            this.nbtTagCompound = reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtDataCompound));
        }

        this.values = loadFromNBTData(this.nbtTagCompound);

    }

    /**
     * Write all the NBT data to an item
     *
     * @param item
     * @return
     */
    public ItemStack write(ItemStack item) {
        Class craftBukkit = reflectionManager.getClass(reflectionManager.CRAFT_BUKKIT + "inventory.CraftItemStack");

        Class NMS = reflectionManager.getClass(reflectionManager.NMS + "ItemStack");

        Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

        Method asNMSCopy = reflectionManager.getMethod(craftBukkit, "asNMSCopy", ItemStack.class);

        Method setTag = reflectionManager.getMethod(NMS, "setTag", nbtDataCompound);

        Method asBukkitCopy = reflectionManager.getMethod(craftBukkit, "asBukkitCopy", NMS);

        Object nmsItem = reflectionManager.invokeMethod(asNMSCopy, null, item);

        Object tag = constructCompound(this.values, nmsItem);

        reflectionManager.invokeMethod(setTag, nmsItem, tag);

        ItemStack itemToReturn = (ItemStack) reflectionManager.invokeMethod(asBukkitCopy, null, nmsItem);
        return itemToReturn;

    }

    /**
     * Loads all values from NMS methods
     *
     * @param nbtData The NBTTagCompound Method
     */
    public Map<String, Object> loadFromNBTData(Object nbtData) {
        Map<String, Object> values = Maps.newHashMap();
        Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

        if (nbtData == null) {
            Constructor constructor = reflectionManager.getConstructor(nbtDataCompound);
            nbtData = reflectionManager.invokeConstructor(constructor);
        }

        Method nbtDataKeySet = reflectionManager.getMethod(nbtDataCompound, "c");

        Method getDataTag = reflectionManager.getMethod(nbtDataCompound, "get", String.class);

        Set<String> o = (Set<String>) reflectionManager.invokeMethod(nbtDataKeySet, nbtData);

        for (String key : o) {//NBTBase value
            /*
             * Display NBT data is somewhat screwed up, ignore it
             */
            if (key.equalsIgnoreCase("display")) {
                continue;
            }

            Object nbtBase = reflectionManager.invokeMethod(getDataTag, nbtData, key);

            values.put(key, this.getDataFrom(nbtBase));
        }
        return values;
    }

    public void setValues(Map<String, Object> obj) {
        this.values = obj;
    }

    public void add(String key, Object value) {
        Object nbtBase = getNBTBase(value);

        Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

        Class nbtBaseClass = reflectionManager.getClass(reflectionManager.NMS + "NBTBase");

        Method set = reflectionManager.getMethod(nbtDataCompound, "set", String.class, nbtBaseClass);

        reflectionManager.invokeMethod(set, this.nbtTagCompound, key, nbtBase);

        this.values.put(key, value);
    }

    private Object constructCompound(Map<String, Object> values, Object item) {
        Object tag;

        Class nmsItemStack = reflectionManager.getClass(reflectionManager.NMS + "ItemStack");

        Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

        Method getTag = reflectionManager.getMethod(nmsItemStack, "getTag");

        Object itemTag = reflectionManager.invokeMethod(getTag, item);
        if (itemTag != null) {
            tag = itemTag;
        } else {
            tag = reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtDataCompound));
        }

        //return toTag(values, tag);
        return this.nbtTagCompound;
    }

    /**
     *
     * @param values NBT Values
     * @return NBTTagCompound made of the given values
     */
    Object toTag(Map<String, Object> values, Object tag) {

        Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");

        Object o = tag == null ? reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtDataCompound)) : tag;

        Class nbtBASE = reflectionManager.getClass(reflectionManager.NMS + "NBTBase");

        Method setTag = reflectionManager.getMethod(nbtDataCompound, "set", String.class, nbtBASE);

        values.forEach((key, value) -> {
            reflectionManager.invokeMethod(setTag, o, key, getNBTBase(value));
        });

        return o;
    }

    Object getNBTBase(Object value) {
        if (value instanceof Byte) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagByte");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, byte.class), value);
        } else if (value instanceof Byte[]) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagByteArray");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, byte[].class), value);
        } else if (value instanceof Short) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagShort");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, short.class), value);
        } else if (value instanceof Integer) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagInt");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, int.class), value);
        } else if (value instanceof Integer[]) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagIntArray");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, int[].class), value);
        } else if (value instanceof Long) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagLong");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, long.class), value);
        } else if (value instanceof Float) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagFloat");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, float.class), value);
        } else if (value instanceof Double) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagDouble");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, double.class), value);
        } else if (value instanceof String) {
            Class nbtTagByte = reflectionManager.getClass(reflectionManager.NMS + "NBTTagString");
            return reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagByte, String.class), value);
        } else if (value instanceof List) {
            Class nbtTagList = reflectionManager.getClass(reflectionManager.NMS + "NBTTagList");
            List<Object> nbtList = Lists.newArrayList();
            ((List) value).forEach(message -> {
                nbtList.add(getNBTBase(message));
            });
            Object nbtTagListObject = reflectionManager.invokeConstructor(reflectionManager.getConstructor(nbtTagList));
            Field list = reflectionManager.getField(nbtTagList, "list");
            reflectionManager.setField(list, nbtTagListObject, nbtList);
            return nbtTagListObject;
        } else if (value instanceof Map) {
            return toTag((Map<String, Object>) value, null);
        } else {
            throw new IllegalArgumentException("Argument not supported");
        }
    }

    Object getDataFrom(Object nbtBase) {
        Class<?> nbtBaseClass = nbtBase.getClass();
        {
            Class nbtDataCompound = reflectionManager.getClass(reflectionManager.NMS + "NBTTagCompound");
            if (nbtDataCompound.isInstance(nbtBase)) {
                return loadFromNBTData(nbtBase);
            }
        }

        {
            Class nbtTagList = reflectionManager.getClass(reflectionManager.NMS + "NBTTagList");
            if (nbtTagList.isInstance(nbtBase)) {
                List<Object> list = Lists.newArrayList();
                Field listField = reflectionManager.getField(nbtTagList, "list");
                //List<NBTBase>
                List<Object> o = (List<Object>) reflectionManager.invokeField(listField, nbtBase);
                o.forEach(nbtBaseInstance -> {
                    list.add(getDataFrom(nbtBaseInstance));
                });
                return list;
            }
        }

        Field data = reflectionManager.getField(nbtBaseClass, "data");
        if (data != null) {
            return reflectionManager.invokeField(data, nbtBase);
        }
        return null;
    }

}
