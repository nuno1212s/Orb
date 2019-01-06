package com.nuno1212s.rediscommunication.newredishandling;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketHandler {

    Priority priority() default Priority.MEDIUM;

    public enum Priority {

        LOWEST,
        LOW,
        MEDIUM,
        HIGH,
        HIGHEST

    }

}
