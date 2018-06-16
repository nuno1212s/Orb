package com.nuno1212s.util;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.util.NBTDataStorage.ReflectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Handles action bar API stuff
 *
 * All credit to the original author
 *
 * @author Connor Linfoot
 */
public class ActionBarAPI {

    private String nmsver;

    public ActionBarAPI() {
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
    }

    public void sendActionBar(Player player, String message) {
        ReflectionManager reflectionManager = ReflectionManager.getIns();
        //Get the craftPlayer class
        Class craftPlayerClass = reflectionManager.getClass(reflectionManager.CRAFT_BUKKIT + "entity.CraftPlayer");
        Object craftPlayerObject = craftPlayerClass.cast(player);
        Object packetPlayOutChatObject;

        //Get all the classes needed
        Class packetPlayOutChat = reflectionManager.getClass(reflectionManager.NMS + "PacketPlayOutChat"),
                packet = reflectionManager.getClass(reflectionManager.NMS + "Packet"),
                chatComponentText = reflectionManager.getClass(reflectionManager.NMS + "ChatComponentText"),
                IChatBaseComponent = reflectionManager.getClass(reflectionManager.NMS + "IChatBaseComponent");

        //Get the necessary constructors
        Constructor chatComponentConstructor = reflectionManager.getConstructor(chatComponentText, String.class),
                packetPlayOutChatConstructor = reflectionManager.getConstructor(packetPlayOutChat, IChatBaseComponent, byte.class);

        Object chatTextObject = reflectionManager.invokeConstructor(chatComponentConstructor, message);

        packetPlayOutChatObject = reflectionManager.invokeConstructor(packetPlayOutChatConstructor, chatTextObject, (byte) 2);

        Method getHandle = reflectionManager.getMethod(craftPlayerClass, "getHandle");
        Object playerHandle = reflectionManager.invokeMethod(getHandle, craftPlayerObject);

        Class<?> NMSPlayerClass = playerHandle.getClass();
        Field playerConnectionField = reflectionManager.getField(NMSPlayerClass, "playerConnection");
        Object playerConnection = reflectionManager.invokeField(playerConnectionField, playerHandle);

        Class<?> playerConnectionClass = playerConnection.getClass();
        Method sendPacket = reflectionManager.getMethod(playerConnectionClass, "sendPacket", packet);
        reflectionManager.invokeMethod(sendPacket, playerConnection, packetPlayOutChatObject);

    }

    /*public void sendActionBar(Player player, String message) {
        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc;
            Class<?> packetPlayOutChat = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            Class<?> c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            Class<?> c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
            ppoc = packetPlayOutChat.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(BukkitMain.getIns(), duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
        while (duration > 60) {
            duration -= 60;
            int sched = duration % 60;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(BukkitMain.getIns(), (long) sched);
        }
    }


}
