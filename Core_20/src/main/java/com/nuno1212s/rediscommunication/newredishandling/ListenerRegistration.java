package com.nuno1212s.rediscommunication.newredishandling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ListenerRegistration {

    private static Logger eventRegister = Logger.getLogger("EventRegister");

    private ConcurrentHashMap<Class<?>, LinkedHashMap<Method, Object>> registeredClasses;

    public ListenerRegistration() {

        registeredClasses = new ConcurrentHashMap<>();

    }

    /**
     * Registers the events in a given class
     *
     * WARNING: Events that handle packets can be called async
     *
     * @param listener
     */
    public void registerEvents(RedisListener listener) {

        Class<? extends RedisListener> registeredClass = listener.getClass();

        Method[] declaredMethods = registeredClass.getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {

            if (declaredMethod.isAnnotationPresent(PacketHandler.class)) {

                PacketHandler annotation = declaredMethod.getAnnotation(PacketHandler.class);

                PacketHandler.Priority priority = annotation.priority();

                // TODO: 06/01/2019 Make the methods inserted sort by priority

                if (declaredMethod.getParameterCount() != 1) {

                    //Miss declared event handler

                    eventRegister.severe("Can't add a packet listener with more than one argument!");

                    continue;

                }

                Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                if (Packet.class.isAssignableFrom(parameterType)) {

                    LinkedHashMap<Method, Object> registeredMethods = registeredClasses.getOrDefault(parameterType, new LinkedHashMap<>());

                    registeredMethods.put(declaredMethod, listener);

                    registeredClasses.put(parameterType, registeredMethods);

                }

            }

        }

    }

    /**
     * Calls the packet arrive event
     * @param packet
     */
    public void callPacketArriveEvent(Packet packet) {

        LinkedHashMap<Method, Object> registeredListeners = this.registeredClasses.get(packet.getClass());

        if (registeredListeners != null && !registeredListeners.isEmpty()) {

            registeredListeners.forEach((method, object) -> {

                try {
                    method.invoke(object, packet);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            });

        }

    }

}
