package com.nuno1212s.util.NBTDataStorage;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Store data in items
 */
public class ReflectionManager {

    Map<String, Class<?>> classCache = new HashMap<>();

    Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<>();

    Map<Class<?>, Map<String, Method>> methodCache = new HashMap<>();

    Map<Class<?>, Map<Class<?>[], Constructor<?>>> constructorCache = new HashMap<>();

    @Getter
    static ReflectionManager ins = new ReflectionManager();

    private String version;

    public String CRAFT_BUKKIT;

    public String NMS;

    public ReflectionManager() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String mcVersion = name.substring(name.lastIndexOf('.') + 1).replace("org.bukkit.craftbukkit.", "");
        version = mcVersion + ".";
        CRAFT_BUKKIT  = "org.bukkit.craftbukkit." + version;
        NMS = "net.minecraft.server." + version;
    }

    public Class<?> getClass(String classPath) {
        if (classCache.containsKey(classPath)) {
            return classCache.get(classPath);
        }
        try {
            Class c = Class.forName(classPath);
            classCache.put(classPath, c);
            return c;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Field getField(Class c, String fieldName) {
        if (fieldCache.containsKey(c)) {
            if (fieldCache.get(c).containsKey(fieldName)) {
                return fieldCache.get(c).get(fieldName);
            }
        }

        try {
            Field declaredField = c.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            Map<String, Field> f;
            if (fieldCache.containsKey(c)) {
                f = fieldCache.get(c);
            } else {
                f = new HashMap<>();
            }
            f.put(fieldName, declaredField);
            fieldCache.put(c, f);
            return declaredField;
        } catch (Exception e) {
            e.printStackTrace();
            for (Field field : c.getDeclaredFields()) {
                System.out.println(field.getName());
            }
        }

        return null;
    }

    public Method getMethod(Class c, String methodName, Class<?>... args) {
        if (methodCache.containsKey(c)) {
            if (methodCache.get(c).containsKey(methodName)) {
                return methodCache.get(c).get(methodName);
            }
        }

        try {
            Method m = c.getDeclaredMethod(methodName, args);
            m.setAccessible(true);
            Map<String, Method> me;
            if (methodCache.containsKey(c)) {
                me = methodCache.get(c);
            } else {
                me = new HashMap<>();
            }
            me.put(methodName, m);
            methodCache.put(c, me);
            return m;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            for (Method method : c.getMethods()) {
                System.out.println(method.getName());
            }
        }

        return null;
    }

    public Constructor getConstructor(Class c, Class<?>... args) {
        if (constructorCache.containsKey(c)) {
            if (constructorCache.get(c).containsKey(args)) {
                return constructorCache.get(c).get(args);
            }
        }
        try {
            Constructor cns = c.getConstructor(args);
            cns.setAccessible(true);
            Map<Class<?>[], Constructor<?>> me;
            if (constructorCache.containsKey(c)) {
                me = constructorCache.get(c);
            } else {
                me = new HashMap<>();
            }
            me.put(args, cns);
            constructorCache.put(c, me);
            return cns;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println(Arrays.asList(c.getConstructors()));
        }
        return null;
    }

    public Object invokeField(Field f, Object classInvoked) {
        try {
            return f.get(classInvoked);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setField(Field f, Object classInvoked, Object newValue) {
        try {
            f.set(classInvoked, newValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object invokeMethod(Method m, Object classInvoked, Object... args) {
        try {
            return m.invoke(classInvoked, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object invokeConstructor(Constructor c, Object... args) {
        try {
            return c.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
